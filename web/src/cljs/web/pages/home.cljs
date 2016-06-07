(ns web.pages.home
  (:require [reagent.core :as r]
            [web.session :as session]))

(defn home-page []
  (if (session/user)
    [:h2.main "Welcome User " (session/user)]
    [:h2.main "Welcome Anonymous!"]))
