(ns utils.mock)

(def query-vector [0.1 0.2 0.3])

(def valid-layer-ns :test-layer)

(def valid-layer
  {:layer-ns valid-layer-ns
   :threshold 0.5
   :model "text-embedding-3-small"
   :aggregation-method :sum})

(def invalid-layer
  {:layer-ns "test-layer"  ;; Should be a keyword
   :threshold "high"       ;; Should be a number
   :model :test-model      ;; Should be a string
   :aggregation-method :sum})

(def additional-layer
  {:layer-ns :additional-layer
   :threshold 0.6
   :model "text-embedding-3-large"
   :aggregation-method :mean})

(def mocked-valid-route-embeddings
  [[0.1 0.2 0.3]
   [0.4 0.5 0.6]
   [0.7 0.8 0.9]])

(def valid-route
  {:name :test-route
   :utterances ["hello" "test" "world"]
   :threshold 0.5
   :embeddings mocked-valid-route-embeddings})

(def invalid-route
  {:name "test-route"      ;; Should be a keyword
   :utterances ["hello" "world"]
   :threshold "high"       ;; Should be a number
   :embeddings [[0.1 0.2 0.3] [0.4 0.5 0.6]]})

(def empty-route
  {:name :empty-route
   :utterances []
   :threshold 0.5})

(def route-update
  {:utterances ["new utterance 1" "new utterance 2"]
   :threshold 0.7})
