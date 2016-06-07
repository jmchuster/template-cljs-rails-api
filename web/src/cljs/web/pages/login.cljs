(ns web.pages.login
  (:require [reagent.core :as r]
            [web.controls :as controls]))

(defn login-button []
  [:div.ui.fluid.large.teal.submit.button
    {:on-click #(controls/login-user! 1)}
    "Login"])

(defn login-page []
  [:div.ui.middle.aligned.center.aligned.grid.main
    [:div.column
      [:h2.ui.teal.image.header
        [:div.content "Login to your account"]]
      [:form.ui.large.form.error
        [:div.ui.stacked.segment
          [:div.field
            [:div.ui.left.icon.input
              [:i.user.icon]
              [:input {:type "text"
                       :name "email"
                       :placeholder "E-mail address"}]]]
          [:div.field
            [:div.ui.left.icon.input
              [:i.lock.icon]
              [:input {:type "password"
                       :name "password"
                       :placeholder "Password"}]]]
          [login-button]]
        [:div.ui.error.message]]]])
