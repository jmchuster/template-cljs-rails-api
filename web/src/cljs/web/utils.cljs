(ns web.utils
  (:require [clojure.string :as string]
            [ajax.core :as ajax]))

;; gives you back a url starting with "#/"
;; which is helpful for assigning to window.location.hash
(defn to_url [path]
  (if (string/starts-with? path "/")
    (str "#" path)
    (str "#/" path)))

;; strips off the # part of the url
(defn current_url []
  (let [hash (.-hash js/window.location)]
    (if (string/starts-with? hash "#")
      (subs hash 1)
      hash)))

(defn redirect_url! [path]
  (set! (.-hash js/window.location) path))

(defn post [url data handler]
  (ajax/POST url
    {:params          data
     :format          :json
     :response-format :json
     :keywords?       true
     :handler         #(handler true %)
     :error-handler   #(handler false (:response %))}))

(defn error-message [response]
  (:detail (nth (:errors response) 0)))
