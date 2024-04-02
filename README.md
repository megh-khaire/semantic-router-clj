# semantic-router-clj

> Decision making layer for LLMs in Clojure

[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.megh/semantic-router-clj.svg)](https://clojars.org/org.clojars.megh/semantic-router-clj)

semantic-router-clj is a Clojure library designed for routing text inputs to predefined routes based on their semantic content. It is inspired by [semantic-router](https://github.com/aurelio-labs/semantic-router) and is useful for guiding chatbot conversations based on topics like chitchat, politics, etc.

## Usage

First, we need to set the API key and create a layer that will handle the routing of messages based on their semantic content.

```clojure
(require '[semantic-router-clj.core as srco]
         '[semantic-router-clj.layer as srcl]
         '[semantic-router-clj.route as srcr])

;; Set API key
(srco/set-api-key "your-api-key")

;; Create a new layer with name -> :first-layer
(srcl/layer :first-layer
            :threshold 0.5
            :model "text-embedding-3-small"
            :aggregation-method :mean)

;; Retrieve the defined layer
(srcl/get-layer :first-layer)
```

We can now define routes within the layer to categorize different types of conversations.
Each route includes a set of utterances that exemplify the kind of messages it should match.

```clojure
;; Define a chitchat route
(srcr/add-route :first-layer
                {:name :chitchat
                 :utterances ["how's the weather today?",
                              "how are things going?",
                              "lovely weather today",
                              "the weather is horrendous",
                              "let's go to the chippy"]})

;; Define a politics route
(srcr/add-route :first-layer
                {:name :politics
                 :utterances ["isn't politics the best thing ever",
                              "why don't you tell me about your political opinions",
                              "don't you just love the president",
                              "they're going to destroy this country!",
                              "they will save the country!"]})

;; Retrieve the defined route for a namespace
(srcr/get-route :first-layer :chitchat)
```

Finally, we can query the layer with a text input to see which route it matches based on its semantic meaning.

```clojure
(router :first-layer "How are you?")
; => :chichat

(router :first-layer "don't you love politics?")
; => :politics

;; When nothing matches we return nil
(router :first-layer "I'm interested in learning about llama 2")
; => nil
```

## License

This project is licensed under the terms of the MIT License.
