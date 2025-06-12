(ns utils.fixtures
  (:require [semantic-router-clj.layer :as layer]
            [semantic-router-clj.route :as srcr]
            [utils.mock :as mock]))


(defn create-mocked-layer
  [mocked-layer]
  (layer/layer (:layer-ns mocked-layer)
               :threshold (:threshold mocked-layer)
               :model (:model mocked-layer)
               :aggregation-method (:aggregation-method mocked-layer)))


(defn reset-layers [f]
  (reset! layer/layer-index {})
  (f)
  (reset! layer/layer-index {}))


(defn with-test-layer [f]
  (create-mocked-layer mock/valid-layer)
  (f)
  (srcr/delete-layer mock/valid-layer-ns))
