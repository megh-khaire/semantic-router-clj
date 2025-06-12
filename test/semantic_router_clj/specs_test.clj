(ns semantic-router-clj.specs-test
  (:require [clojure.test :refer [is deftest testing]]
            [semantic-router-clj.specs :as specs]
            [utils.exceptions :as ue]
            [utils.mock :as mock]))

(deftest test-validate-route
  (testing "route validator"
    ;; validate-route :create
    (is (nil? (specs/validate-route mock/valid-route
                                    :create)))
    (ue/assert-validation-exception specs/validate-route
                                    mock/invalid-route
                                    :create)

    ;; validate-route :update
    (is (nil? (specs/validate-route (dissoc mock/valid-route :name)
                                    :update)))
    (ue/assert-validation-exception specs/validate-route
                                    (dissoc mock/invalid-route :name)
                                    :update)

    ;; validate-route invalid method
    (ue/assert-validation-method-exception specs/validate-route
                                           mock/valid-route
                                           :delete)))

(deftest test-validate-layer
  (testing "layer validator"
    ;; validate-layer :create
    (is (nil? (specs/validate-layer mock/valid-layer
                                    :create)))
    (ue/assert-validation-exception specs/validate-layer
                                    mock/invalid-layer
                                    :create)

    ;; validate-layer :update
    (is (nil? (specs/validate-layer (dissoc mock/valid-layer :layer-ns)
                                    :update)))
    (ue/assert-validation-exception specs/validate-layer
                                    (dissoc mock/invalid-layer :layer-ns)
                                    :update)

    ;; validate-layer invalid method
    (ue/assert-validation-method-exception specs/validate-layer
                                           mock/valid-layer
                                           :delete)))
