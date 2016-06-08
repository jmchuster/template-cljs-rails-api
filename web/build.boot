(set-env!
 :source-paths    #{"src/cljs" "css"}
 :resource-paths  #{"resources" "semantic/dist"}
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
                 [deraen/boot-sass               "0.2.1"]
                 [crisptrutski/boot-cljs-test    "0.2.1"]
                 [me.raynes/conch                "0.8.0"]
                 ;; clojurescript dependencies
                 [reagent                        "0.6.0-alpha2"]
                 [secretary                      "1.2.3"]
                 [cljsjs/semantic-ui             "2.1.8-0"]
                 [com.andrewmcveigh/cljs-time    "0.4.0"]
                 [cljs-ajax                      "0.5.5"]
                 ;; bower dependencies
                 [org.webjars.bower/font-awesome "4.6.3"]])

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[pandeiro.boot-http    :refer [serve]]
 '[crisptrutski.boot-cljs-test :refer [test-cljs]]
 '[deraen.boot-sass      :refer [sass]]
 '[me.raynes.conch.low-level :as conch])

(defn start-gulp-watch! []
  (let [gulp (conch/proc "gulp" "watch" "--gulpfile=/home/app/web/semantic/gulpfile.js")]
    (future (conch/stream-to-out gulp :out))
    (future (conch/stream-to gulp :err System/err))
    gulp))

(deftask gulp-watch []
  (let [gulp (delay (start-gulp-watch!))]
    (cleanup (conch/destroy @gulp))
    (with-pass-thru _ @gulp)))

(deftask build []
  (comp (speak)
        (cljs)
        (sass)
        (sift :move {#"^(main\.css(?:\.map)?)$" "css/$1"})
        (target :dir #{"target"})))

(deftask run []
  (comp (watch)
        (cljs-repl)
        (reload)
        (build)
        (gulp-watch)))

(deftask production []
  (task-options! cljs {:optimizations :advanced})
  identity)

(deftask prod []
  (comp (production)
        (build)))

(deftask development []
  (task-options! cljs      {:optimizations :none :source-map true}
                 reload    {:on-jsload 'web.app/init
                            :port 43210}
                 cljs-repl {:nrepl-opts {:bind "0.0.0.0"
                                         :port 7888}
                            :port        45678
                            :ws-host     "my_app.docker"
                            :ip          "0.0.0.0"})
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
