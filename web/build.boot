(set-env!
 :source-paths    #{"src/cljs" "css" "semantic"}
 :resource-paths  #{"resources"}
 :dependencies '[;; boot dependencies
                 [adzerk/boot-cljs               "1.7.228-1"]
                 [adzerk/boot-cljs-repl          "0.3.0"]
                 [com.cemerick/piggieback        "0.2.1"]
                 [weasel                         "0.7.0"]
                 [org.clojure/tools.nrepl        "0.2.12"]
                 [adzerk/boot-reload             "0.4.8"]
                 [pandeiro/boot-http             "0.7.3"]
                 [org.slf4j/slf4j-nop            "1.7.21"]
                 [org.clojure/clojurescript      "1.9.14"]
                 [deraen/boot-less               "0.5.0"] ; (https://github.com/Deraen/less4clj)
                 [deraen/boot-sass               "0.2.1"]
                 [crisptrutski/boot-cljs-test    "0.2.1"]
                 ;; clojurescript dependencies
                 [reagent                        "0.6.0-alpha2"]
                 [secretary                      "1.2.3"]
                 [cljsjs/semantic-ui             "2.1.8-0"]
                 [com.andrewmcveigh/cljs-time    "0.4.0"]
                 ;; bower dependencies
                 [org.webjars.bower/font-awesome "4.6.3"]])

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[pandeiro.boot-http    :refer [serve]]
 '[crisptrutski.boot-cljs-test :refer [test-cljs]]
 '[deraen.boot-less      :refer [less]]
 '[deraen.boot-sass      :refer [sass]])

(deftask build []
  (comp (speak)
        (cljs)
        ; (less)
        (sass)
        (sift :move {#"^(semantic\.css(?:\.map)?)$" "css/$1"
                     #"^(main\.css(?:\.map)?)$" "css/$1"})))

(deftask run []
  (comp (serve)
        (watch)
        (cljs-repl)
        (reload)
        (build)))

(deftask production []
  (task-options! cljs {:optimizations :advanced}
                 less {:compression true})
  identity)

(deftask prod []
  (comp (production)
        (build)
        (target :dir #{"target"})))

(deftask development []
  (task-options! cljs      {:optimizations :none :source-map true}
                 reload    {:on-jsload 'web.app/init
                            :port 43210}
                 cljs-repl {:nrepl-opts {:bind "0.0.0.0"
                                         :port 7888}
                            :port        45678
                            :ws-host     "my_app.docker"
                            :ip          "0.0.0.0"}
                 less      {:source-map true})
  identity)

(deftask dev
  "Simple alias to run application in development mode"
  []
  (comp (development)
        (run)))

(deftask testing []
  (set-env! :source-paths #(conj % "test/cljs"))
  identity)

;;; This prevents a name collision WARNING between the test task and
;;; clojure.core/test, a function that nobody really uses or cares
;;; about.
(ns-unmap 'boot.user 'test)

(deftask test []
  (comp (testing)
        (test-cljs :js-env :phantom
                   :exit?  true)))

(deftask auto-test []
  (comp (testing)
        (watch)
        (test-cljs :js-env :phantom)))
