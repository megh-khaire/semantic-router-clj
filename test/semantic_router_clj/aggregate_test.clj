(ns semantic-router-clj.aggregate-test
  (:require [bond.james :as bond]
            [clojure.core.matrix.stats :as stats]
            [clojure.test :refer [is deftest]]
            [semantic-router-clj.aggregate :as agg]
            [utils.mock :as mock]))

(defn approx= [a b delta]
  (cond
    (and (number? a) (number? b))
    (<= (Math/abs (- a b)) delta)

    (and (sequential? a)
         (sequential? b))
    (every? true? (map #(<= (Math/abs (- %1 %2)) delta) a b))

    :else
    (throw (IllegalArgumentException. "Both arguments must be numbers or sequences of numbers"))))

(deftest test-aggregation-method?
  (is (agg/aggregation-method? :sum))
  (is (agg/aggregation-method? :max))
  (is (agg/aggregation-method? :mean))
  (is (not (agg/aggregation-method? :median))))

(deftest test-get-aggregation-fn
  (is (= 6 ((agg/get-aggregation-fn :sum) [1 2 3])))
  (is (= 3 ((agg/get-aggregation-fn :max) [1 2 3])))
  (is (= 2 ((agg/get-aggregation-fn :mean) [1 2 3])))
  (is (thrown? IllegalArgumentException (agg/get-aggregation-fn :median))))

(deftest test-get-similarity-scores
  (bond/with-spy [stats/cosine-similarity]
    (let [result (agg/get-similarity-scores mock/query-vector mock/valid-route)]
      (is (approx= result [1.0, 0.976, 0.96] 0.01))
      (is (= 3 (count (bond/calls stats/cosine-similarity)))))))

(deftest test-get-aggregated-similarity
  (bond/with-spy [agg/get-similarity-scores]
    (is (approx= 2.936
                 (agg/get-aggregated-similarity mock/query-vector
                                                mock/valid-route
                                                (agg/get-aggregation-fn :sum))
                 0.01))
    (is (= 1 (count (bond/calls agg/get-similarity-scores))))
    (is (approx= 0.9787 (agg/get-aggregated-similarity mock/query-vector
                                                       mock/valid-route
                                                       (agg/get-aggregation-fn :mean))
                 0.01))
    (is (= 1.0 (agg/get-aggregated-similarity mock/query-vector
                                              mock/valid-route
                                              (agg/get-aggregation-fn :max)))))
  (bond/with-stub [[agg/get-similarity-scores (constantly [])]]
    (is (approx= 0 (agg/get-aggregated-similarity mock/query-vector
                                                  mock/valid-route
                                                  (agg/get-aggregation-fn :sum))
                 0.01))
    (is (= 1 (count (bond/calls agg/get-similarity-scores)))))
  (let [route {:threshold 0.5 :embeddings []}]
    (is (approx= 0 (agg/get-aggregated-similarity mock/query-vector
                                                  route
                                                  (agg/get-aggregation-fn :sum))
                 0.01))))
