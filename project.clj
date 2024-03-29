(defproject semantic-router-clj "0.1.0"
  :description "Decision making layer for LLMs in Clojure"
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :main ^:skip-aot semantic-router-clj.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
