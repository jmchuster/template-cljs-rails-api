(ns web.app
  (:require [reagent.core :as r]))

(defn app []
  [:div
    "My_Web!"])

(defn init []
  (r/render-component [app]
    (.getElementById js/document "container")))
