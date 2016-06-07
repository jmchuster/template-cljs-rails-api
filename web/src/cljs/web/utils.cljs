(ns web.utils
  (:require [clojure.string :as string]))

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
