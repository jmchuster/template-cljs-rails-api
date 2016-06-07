(ns web.session
  (:require [reagent.core :as r]))

(defonce state (r/atom {}))

(defn user []
  (:user @state))

(defn page []
  (:page @state))

(defn set-page! [page]
  (swap! state assoc-in [:page] page))

(defn set-user! [user]
  (swap! state assoc-in [:user] user))
