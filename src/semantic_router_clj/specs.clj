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
  [route method]
  (when-not
   (case method
     :create (m/validate Route route)
     :update (m/validate Route-Update route)
     nil)
    (throw (ex-info "Invalid Route" route))))


(defn validate-layer
  [layer method]
  (when-not
   (case method
     :create (m/validate Layer layer)
     :update (m/validate Layer-Update layer)
     nil)
    (throw (ex-info "Invalid Layer" layer))))
