(ns semantic-router-clj.layer
  (:require [semantic-router-clj.aggregate :as srca]
            [semantic-router-clj.specs :as srcs]
            [semantic-router-clj.openai :as srco]))

;; Atom to hold the index of layers
(def layer-index (atom {}))


(defn get-layer
  "Return the layer associated with the given layer-ns, or nil if not found."
  [layer-ns]
  (get @layer-index layer-ns nil))


(defn layer
  "Creates a layer with the given settings: layer-ns, threshold, model, and aggregation-method."
  [layer-ns & {:keys [threshold model aggregation-method]
               :or {threshold 0
                    model "text-embedding-3-small"
                    aggregation-method :mean}}]
  {:pre [(keyword? layer-ns)
         (srco/embedding-model? model)
         (srca/aggregation-method? aggregation-method)]}
  (let [layer {:layer-ns layer-ns
               :threshold threshold
               :model model
               :aggregation-method aggregation-method}]
    (srcs/validate-layer layer :create)
    (swap! layer-index assoc layer-ns layer)))


(defn update-layer
  "Updates a layer with the given settings: layer-ns, threshold, model, and aggregation-method."
  [layer-ns & {:keys [_threshold _model _aggregation-method] :as layer}]
  (srcs/validate-layer layer :update)
  (if (get @layer-index layer-ns nil)
    (swap! layer-index update layer-ns merge layer)
    (throw (ex-info "Layer does not exist"
                    {:layer layer}))))
