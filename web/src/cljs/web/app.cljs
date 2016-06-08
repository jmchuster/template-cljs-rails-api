(ns web.app
  (:require [reagent.core :as r]
            [web.routes]
            [web.components.header :refer [header]]
            [web.components.footer :refer [footer]]
            [web.session :as session]
            [web.pages.login :refer [login-page]]
            [web.controls :as controls]))

(defn authenticate-user? [page]
  (:authenticate_user (meta (page))))

(defn app []
  [:div
    [header]
    [:div.pusher
      (if (and (authenticate-user? (session/page)) (nil? (session/user)))
        [login-page]
        [(session/page)])
      [footer]]])

(defn init []
  (controls/refresh-user!)
  (r/render-component [app]
    (.getElementById js/document "container")))
