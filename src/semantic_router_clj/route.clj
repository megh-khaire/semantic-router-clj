(ns semantic-router-clj.route
  (:require [cheshire.core :refer [generate-string]]
            [clojure.set :as cset]
            [semantic-router-clj.specs :as srcs]
            [semantic-router-clj.layer :as srcl]
            [semantic-router-clj.openai :as srco]
            [semantic-router-clj.aggregate :as srca]))

(def route-index (atom {}))


(defn get-route
  "Gets route from layer"
  [layer-ns route-name]
  (get-in @route-index [layer-ns route-name] nil))


(defn add-route
  "Adds a new route in the given layer-ns

   Note: Ensure that the layer-ns is defined before using this function.
   THIS FUNCTION WILL OVERWRITE ANY EXISTING ROUTE WITH THE SAME NS & NAME"
  [layer-ns {:keys [threshold] :as route}]
  (if-let [layer (srcl/get-layer layer-ns)]
    (let [payload (generate-string {:model (:model layer)
                                    :input (:utterances route)})
          embeddings (srco/generate-embeddings payload)
          updated-route (assoc route
                               :embeddings embeddings
                               :threshold (or threshold 0))]
      (srcs/validate-route updated-route :create)
      (swap! route-index assoc-in [layer-ns (:name route)] updated-route))
    (throw (ex-info "Invalid layer-ns, are you sure you have defined a layer?"
                    {:layer-ns layer-ns
                     :route route}))))


(defn- update-route-utternaces
  [index-route layer-ns utterances]
  (let [new-utterances (cset/difference utterances
                                        (:utterances index-route))]
    (if (seq new-utterances)
      (let [layer (srcl/get-layer layer-ns)
            payload (generate-string {:model (:model layer)
                                      :input new-utterances})
            embeddings (srco/generate-embeddings payload)]
        (-> index-route
            (update :utterances concat new-utterances)
            (update :embeddings concat embeddings)))
      index-route)))


(defn- update-route-threshold
  [index-route threshold]
  (assoc index-route :threshold threshold))


(defn update-route
  "Updates route with the given name in the given layer-ns

   Note: Ensure that the route is defined before using this function.
   THIS FUNCTION WILL ADD DATA TO EXISTING ROUTE"
  [layer-ns route-name route]
  (if-let [index-route (get-route layer-ns route)]
    (cond-> index-route
      (:utterances route) (update-route-utternaces layer-ns
                                                   (:utterances route))

      (:threshold route) (update-route-threshold (:threshold route))

      :always (fn [index-route*]
                (srcs/validate-route index-route* :update)
                (swap! route-index assoc-in [layer-ns route-name] index-route*)))
    (throw (ex-info "Invalid route, are you sure this route exists?"
                    {:layer-ns layer-ns
                     :route route}))))


(defn delete-route
  "Removes route from layer"
  [layer-ns route-name]
  (swap! route-index update layer-ns dissoc route-name))


(defn delete-layer
  "Removes the layer associated with the specified layer-ns from the index.
   Also clears all the routes"
  [layer-ns]
  (swap! srcl/layer-index dissoc layer-ns)
  (swap! route-index dissoc layer-ns))


(defn get-top-route
  "For the given layer returns the route with highest semantic score
   based on the given aggregation method"
  [{:keys [layer-ns threshold aggregation-method]} query-vector]
  (let [routes (layer-ns @route-index)
        aggregation-fn (srca/get-aggregation-fn aggregation-method)
        route->scores (reduce-kv
                       (fn [acc route-name route]
                         (assoc acc
                                route-name
                                (srca/get-aggregated-similarity query-vector
                                                                (update route :threshold max threshold)
                                                                aggregation-fn)))
                       {}
                       routes)
        top-route (when (not-empty route->scores)
                    (apply max-key val route->scores))]
    (when (and top-route
               (> (val top-route) 0))
      (key top-route))))
