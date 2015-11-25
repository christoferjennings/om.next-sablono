(set-env!
 :source-paths #{"src"}
 :resource-paths #{"html"}
 :dependencies '[[org.clojure/clojure   "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [adzerk/boot-cljs      "1.7.170-3"  :scope "test"]
                 [adzerk/boot-cljs-repl "0.3.0"]
                 [com.cemerick/piggieback "0.2.1"    :scope "test"]
                 [weasel                  "0.7.0"    :scope "test"]
                 [org.clojure/tools.nrepl "0.2.12"   :scope "test"]
                 [adzerk/boot-reload    "0.4.2"]
                 [pandeiro/boot-http    "0.6.3"      :scope "test"]
                 [org.omcljs/om         "1.0.0-alpha23"]
                 [sablono               "0.4.0"]])

(require '[adzerk.boot-cljs      :refer [cljs]]
         '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
         '[adzerk.boot-reload    :refer [reload]]
         '[pandeiro.boot-http    :refer [serve]])

(deftask dev []
  (comp (serve :dir "target/")
        (watch)
        (speak)
        (reload :on-jsload 'scratch.web/main)
        (cljs-repl)
        (cljs :source-map true :optimizations :none)))

(deftask build []
  (comp (cljs :optimizations :advanced)))
