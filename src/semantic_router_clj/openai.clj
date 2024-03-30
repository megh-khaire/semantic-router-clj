(ns semantic-router-clj.openai
  (:require [cheshire.core :as cc]
            [org.httpkit.client :as http])
  (:import [com.fasterxml.jackson.core JsonParseException]))

(def api-key nil)
(def embedding-model? #{"text-embedding-3-small"
                        "text-embedding-ada-002"
                        "text-embedding-3-large"})


(defn set-api-key
  [value]
  (alter-var-root #'api-key (constantly value)))


(defn extract-embeddings
  [data]
  (reduce (fn [acc {:keys [embedding]}]
            (conj acc embedding))
          []
          data))


(defn generate-embeddings
  "Makes a POST request to a specified LLM API endpoint with given headers and body."
  [body]
  (try
    (-> (http/post "https://api.openai.com/v1/embeddings"
                   {:headers {"Authorization" (str "Bearer " api-key)
                              "Content-Type" "application/json"}
                    :body body})
        deref ;; Dereference the future
        :body
        (cc/parse-string true)
        :data
        extract-embeddings)
    (catch JsonParseException _)))


(comment
  (set-api-key "<API-KEY>")
  (def payload (cc/generate-string {:input "Test Data"
                                    :model "text-embedding-3-small"}))

  (generate-embeddings payload))
