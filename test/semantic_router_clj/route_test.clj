(ns semantic-router-clj.route-test
  (:require [bond.james :as bond]
            [clojure.set :as cset]
            [clojure.test :refer [deftest testing is use-fixtures]]
            [semantic-router-clj.openai :as srco]
            [semantic-router-clj.route :as srcr]
            [utils.fixtures :refer [with-test-layer]]
            [utils.mock :as mock]))


(use-fixtures :each with-test-layer)


(deftest get-routes-test
  (testing "get-routes"
    ;; Check if routes map is empty initially
    (is (empty? (srcr/get-routes mock/valid-layer-ns)))

    ;; Add a route and check if it appears in the routes map
    (bond/with-stub! [[srco/generate-embeddings (constantly mock/mocked-valid-route-embeddings)]]
      (srcr/add-route mock/valid-layer-ns mock/valid-route)
      (is (= {(:name mock/valid-route) mock/valid-route}
             (srcr/get-routes mock/valid-layer-ns))))))

(deftest get-route-test
  (testing "get-route"
    ;; Check if non-existent route returns nil
    (is (nil? (srcr/get-route mock/valid-layer-ns :non-existent-route)))

    ;; Add a route and check if it can be retrieved
    (bond/with-stub! [[srco/generate-embeddings (constantly mock/mocked-valid-route-embeddings)]]
      (srcr/add-route mock/valid-layer-ns mock/valid-route)
      (is (= mock/valid-route
             (srcr/get-route mock/valid-layer-ns (:name mock/valid-route)))))))

(deftest add-route-test
  (testing "add-route"
    ;; Add a valid route
    (bond/with-stub! [[srco/generate-embeddings (constantly mock/mocked-valid-route-embeddings)]]
      (is (= (:name mock/valid-route)
             (srcr/add-route mock/valid-layer-ns mock/valid-route)))
      (is (= 1 (count (bond/calls srco/generate-embeddings)))))

    ;; Try adding route to non-existent layer
    (is (thrown? clojure.lang.ExceptionInfo
                 (srcr/add-route :non-existent-layer mock/valid-route)))

    ;; Try adding route with empty utterances
    (is (thrown? clojure.lang.ExceptionInfo
                 (srcr/add-route mock/valid-layer-ns mock/empty-route)))

    ;; Try adding invalid route
    (is (thrown? clojure.lang.ExceptionInfo
                 (srcr/add-route mock/valid-layer-ns mock/invalid-route)))))

(deftest update-route-test
  (testing "update-route"
    (bond/with-stub! [[srco/generate-embeddings (constantly mock/mocked-valid-route-embeddings)]]
      ;; Add a route
      (srcr/add-route mock/valid-layer-ns mock/valid-route)
      ;; Update route utterances
      (srcr/update-route mock/valid-layer-ns
                         (:name mock/valid-route)
                         (select-keys mock/route-update [:utterances]))
      (let [expected-utterances (cset/union (set (:utterances mock/valid-route))
                                                   (set (:utterances mock/route-update)))]
        (is (= expected-utterances
               (set (:utterances (srcr/get-route mock/valid-layer-ns
                                                 (:name mock/valid-route)))))))

      ;; Update route threshold
      (srcr/update-route mock/valid-layer-ns
                         (:name mock/valid-route)
                         {:threshold 0.8})
      (is (= 0.8
             (:threshold (srcr/get-route mock/valid-layer-ns (:name mock/valid-route)))))

      ;; Try updating non-existent route
      (is (thrown? clojure.lang.ExceptionInfo
                   (srcr/update-route mock/valid-layer-ns
                                      :non-existent-route
                                      mock/route-update))))))

(deftest delete-route-test
  (testing "delete-route"
    ;; Delete existing route
    (bond/with-stub! [[srco/generate-embeddings (constantly mock/mocked-valid-route-embeddings)]]
      (srcr/add-route mock/valid-layer-ns mock/valid-route)
      (srcr/delete-route mock/valid-layer-ns (:name mock/valid-route))
      (is (nil? (srcr/get-route mock/valid-layer-ns (:name mock/valid-route)))))

    ;; Try deleting non-existent route (should not throw)
    (is (srcr/delete-route mock/valid-layer-ns :non-existent-route))))

