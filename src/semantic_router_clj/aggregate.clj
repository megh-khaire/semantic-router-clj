(ns semantic-router-clj.aggregate
  (:require [clojure.core.matrix.stats :as stats]))


(def aggregation-method? #{:sum :max :mean})


(defn get-aggregation-fn
  "Returns a function that will be used to aggregate the similarity scores"
  [method]
  (case method
    :sum #(reduce + 0 %)
    :max (partial apply max)
    :mean #(/ (reduce + 0 %) (count %))
    (throw (IllegalArgumentException. (str "Unsupported method: " method)))))


(defn- get-similarity-scores
  "Compute the similarity scores between a query vector and a set of vectors"
  [query-vector route]
  (reduce (fn [similar-vectors index-vector]
            (let [similarity (stats/cosine-similarity query-vector index-vector)]
              (if (> similarity (:threshold route))
                (conj similar-vectors similarity)
                similar-vectors)))
          []
          (:embeddings route)))


(defn get-aggregated-similarity
  "Calculates the aggregated similarty score for given route"
  [query-vector route aggregation-fn]
  (let [similarity-scores (get-similarity-scores query-vector route)]
    (if (not-empty similarity-scores)
      (aggregation-fn similarity-scores)
      0)))
