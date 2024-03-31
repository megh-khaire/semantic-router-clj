(ns semantic-router-clj.specs
  (:require [malli.core :as m]))

(def Route
  [:map
   [:name :keyword]
   [:utterances [:vector string?]]
   [:threshold {:optional true} [:or :int :double]]
   [:embeddings [:vector [:vector :double]]]])

(def Route-Update
  [:map
   [:utterances {:optional true} [:vector :string]]
   [:threshold {:optional true} [:or :int :double]]
   [:embeddings [:vector [:vector :double]]]])

(def Layer
  [:map
   [:layer-ns :keyword]
   [:threshold [:or :int :double]]
   [:model :string]
   [:aggregation-method :keyword]])

(def Layer-Update
  [:map
   [:threshold {:optional true} [:or :int :double]]
   [:model {:optional true} :string]
   [:aggregation-method {:optional true} :keyword]])


(defn validate-route
  "Validates a route against a schema based on the specified method (:create or :update).
   Throws an exception if validation fails."
  [route method]
  (if-let [schema (case method
                    :create Route
                    :update Route-Update
                    nil)]
    (when-not (m/validate schema route)
      (throw (ex-info "Spec validation failed for route"
                      {:explaination (m/explain schema route)
                       :route route})))
    (throw (ex-info "Invalid route validation method"
                    {:method method}))))


(defn validate-layer
  "Validates a layer against a schema based on the specified method (:create or :update).
   Throws an exception if validation fails."
  [layer method]
  (if-let [schema (case method
                    :create Layer
                    :update Layer-Update
                    nil)]
    (when-not (m/validate schema layer)
      (throw (ex-info "Spec validation failed for layer"
                      {:explaination (m/explain schema layer)
                       :layer layer})))
    (throw (ex-info "Invalid layer validation method"
                    {:method method}))))
