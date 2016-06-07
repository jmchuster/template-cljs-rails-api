(ns web.routes
  (:require [reagent.core :as r]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [web.controls :as controls]
            [web.session :as session]
            [web.pages.home :refer [home-page]]
            [web.pages.login :refer [login-page]]
            [web.pages.users :refer [users-page]]
            [web.pages.error-404 :refer [error-404-page]]
            [web.utils :as utils])
  (:import goog.History))

(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (controls/render-page! home-page))

(secretary/defroute "/login" []
  (controls/render-page! login-page))

(secretary/defroute "/logout" []
  (controls/logout-user!)
  (utils/redirect_url! "/"))

(secretary/defroute "/users" []
  (controls/render-page! users-page))

(secretary/defroute "/*" []
  (controls/render-page! error-404-page))

(let [h (History.)]
  (goog.events/listen h EventType/NAVIGATE #(secretary/dispatch! (.-token %)))
  (doto h (.setEnabled true)))
