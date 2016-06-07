(ns web.controls
  (:require [reagent.core :as r]
            [web.session :as session]
            [web.utils :as utils]))

(defn login-user! [username password]
  (utils/post "/api/sessions"
    {:username username
     :password password}
    (fn [ok response]
      (if ok
        (do
          (session/set-user! (:data response))
          (if (= (utils/current_url) "/login") (utils/redirect_url! "/")))
        (do
          (js/alert (utils/error-message response)))))))

(defn logout-user! []
  (session/set-user! nil))

(defn render-page! [page-fn]
  (session/set-page! page-fn))
