(ns semantic-router-clj.layer-test
  (:require [clojure.test :refer [is deftest testing use-fixtures]]
            [semantic-router-clj.layer :as layer]
            [utils.fixtures :refer [reset-layers create-mocked-layer]]
            [utils.mock :as mock]))

(use-fixtures :each reset-layers)

(deftest test-get-layer
  (testing "get-layer"
    ;; Create a layer and check if it can be retrieved
    (create-mocked-layer mock/valid-layer)
    (is (= mock/valid-layer (layer/get-layer mock/valid-layer-ns)))
    (is (nil? (layer/get-layer :nonexistent-layer)))))

(deftest test-get-layers
  (testing "get-layers"
    ;; Check if the layers map is empty initially
    (is (empty? (layer/get-layers)))
    ;; Create a layer and check if it appears in the layers map
    (create-mocked-layer mock/valid-layer)
    (is (= {mock/valid-layer-ns mock/valid-layer} (layer/get-layers)))
    ;; Create another layer and check if both appear in the layers map
    (create-mocked-layer mock/additional-layer)
    ;; Check if both layers are present in the layers map
    (is (= {mock/valid-layer-ns mock/valid-layer
            (:layer-ns mock/additional-layer) mock/additional-layer}
           (layer/get-layers)))))

(deftest test-create-layer
  (testing "create-layer"
    ;; Create a layer and check if it can be retrieved
    (is (= mock/valid-layer (-> mock/valid-layer
                                create-mocked-layer
                                (get mock/valid-layer-ns))))
    (is (= mock/valid-layer (layer/get-layer mock/valid-layer-ns)))
    ;; Check if an exception is thrown for a layer with an invalid arguments
    (is (thrown? java.lang.AssertionError
                 (create-mocked-layer mock/invalid-layer)))))

(deftest test-update-layer
  (testing "update-layer"
    ;; Create a layer to update
    (create-mocked-layer mock/valid-layer)
    ;; Update the layer with valid arguments
    (layer/update-layer mock/valid-layer-ns
                        :threshold 0.6
                        :aggregation-method :mean)
    ;; Check if the layer has been updated correctly
    (is (= (assoc mock/valid-layer
                  :threshold 0.6
                  :aggregation-method :mean)
           (layer/get-layer mock/valid-layer-ns)))
    ;; Try updating a non-existent layer
    (is (thrown? clojure.lang.ExceptionInfo
                 (layer/update-layer :nonexistent-layer
                                     :threshold 0.7
                                     :model "text-embedding-3-large"
                                     :aggregation-method :max)))
    ;; Try an invalid update
    (is (thrown? clojure.lang.ExceptionInfo
                 (layer/update-layer mock/valid-layer-ns
                                     :threshold "high"
                                     :model "text-embedding-3-large"
                                     :aggregation-method :sum)))))
