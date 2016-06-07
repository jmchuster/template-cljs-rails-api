(ns web.pages.users
  (:require [reagent.core :as r]))

(defn users-page []
  ^{:authenticate_user true}
  [:h2.main "Administrate users!"])
