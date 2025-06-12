(ns utils.exceptions
  (:require [clojure.test :refer [is]]))

(defn assert-validation-exception [f & args]
  (try
    (apply f args)
    (is false "Expected validation exception")
    (catch Exception e
      (is (some #(= (ex-message e) %)
                ["Spec validation failed for route" "Spec validation failed for layer"]))
      (is (map? (ex-data e))))))

(defn assert-validation-method-exception [f & args]
  (try
    (apply f args)
    (is false "Expected invalid method exception")
    (catch Exception e
      (is (some #(= (ex-message e) %)
                ["Invalid route validation method"
                 "Invalid layer validation method"]))
      (is (map? (ex-data e))))))
