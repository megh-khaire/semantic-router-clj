(ns semantic-router-clj.core
  (:gen-class)
  (:require [cheshire.core :as cc]
            [semantic-router-clj.layer :as srcl]
            [semantic-router-clj.openai :as srco]
            [semantic-router-clj.route :as srcr]))


(defn router
  "Routes a query to the top matching layer based on the namespace `layer-ns`.
   Generates an embedding for the query and uses it to find the best route."
  [layer-ns query]
  {:pre [(keyword? layer-ns)]}
  (srco/validate-api-key)
  (let [layer (srcl/get-layer layer-ns)
        embedding (-> {:input query}
                      (assoc :model (:model layer))
                      (cc/generate-string true)
                      srco/generate-embeddings
                      first)]
    (srcr/get-top-route layer embedding)))


;; Usage example
(comment
  ;; Set API key
  (srco/set-api-key "<api-key>")

  ;; Create a new layer
  (srcl/layer :test
              :threshold 0.5
              :model "text-embedding-3-small"
              :aggregation-method :mean)

  (srcl/get-layer :test)

  ;; Define routes for the layer
  (srcr/add-route :test
                  {:name :chitchat
                   :utterances ["how's the weather today?",
                                "how are things going?",
                                "lovely weather today",
                                "the weather is horrendous",
                                "let's go to the chippy"]})

  (srcr/get-route :test :chitchat)

  (srcr/add-route :test
                  {:name :politics
                   :utterances ["isn't politics the best thing ever",
                                "why don't you tell me about your political opinions",
                                "don't you just love the president",
                                "they're going to destroy this country!",
                                "they will save the country!"]})

  ;; Query the layer
  (router :test "How are you?")

  (router :test "I'm interested in learning about llama 2")

  (srcl/update-layer :test
                     :threshold 0.5
                     :aggregation-method :mean)

  (router :test "don't you love politics?"))
