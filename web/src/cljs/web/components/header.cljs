(ns web.components.header
  (:require [reagent.core :as r]
            [web.routes :as routes]
            [web.session :as session]
            [web.utils :as utils]))

(defn logged-in-header []
  [:div.ui.fixed.inverted.menu
    [:div.ui.container
      [:a.header.item {:href (utils/to_url "/")} "My_App"]
      [:a.item {:href (utils/to_url "/users")} "Users"]
      [:div.ui.simple.dropdown.item "Account"
        [:i.dropdown.icon]
        [:div.menu
          [:a.item {:href (utils/to_url "/logout")} "Logout"]]]]])

(defn logged-out-header []
  [:div.ui.fixed.inverted.menu
    [:div.ui.container
      [:a.header.item {:href (utils/to_url "/")} "My_App"]
      [:a.item {:href (utils/to_url "/login")} "Login"]]])

(defn header []
  (if (session/user)
    [logged-in-header]
    [logged-out-header]))
