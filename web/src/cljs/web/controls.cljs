(ns web.controls
  (:require [reagent.core :as r]
            [web.session :as session]
            [web.utils :as utils]))

(defn refresh-user! []
  (when (nil? (session/user))
    (utils/get "/api/session"
      (fn [ok response]
        (when ok
          (session/set-user! (:data response))
          (if (= (utils/current_url) "/login") (utils/redirect_url! "/")))))))

(defn login-user! [username password]
  (utils/post "/api/session"
    {:username username
     :password password}
    (fn [ok response]
      (when ok
        (session/set-user! (:data response))
        (if (= (utils/current_url) "/login") (utils/redirect_url! "/")))
      (when-not ok
        (js/alert (utils/error-message response))))))

(defn logout-user! []
  (session/set-user! nil))

(defn render-page! [page-fn]
  (session/set-page! page-fn))
