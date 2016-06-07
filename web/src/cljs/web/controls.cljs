(ns web.controls
  (:require [reagent.core :as r]
            [web.session :as session]
            [web.utils :as utils]))

(defn login-user! [username password]
  (session/set-user! {:username username :password password})
  (if (= (utils/current_url) "/login") (utils/redirect_url! "/")))

(defn logout-user! []
  (session/set-user! nil))

(defn render-page! [page-fn]
  (session/set-page! page-fn))
