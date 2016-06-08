(ns web.pages.login
  (:require [reagent.core :as r]
            [web.controls :as controls]))

(defn input-text [val hash]
  [:input
    (merge {:type "text"
            :value @val
            :on-change #(reset! val (-> % .-target .-value))}
           hash)])

(defn handle-login [state]
  (controls/login-user! (:email @state) (:password @state)))

(defn handle-keyup [event state]
  (when (-> event .-key (= "Enter"))
    (handle-login state)))

(defn input-username [state]
  [:div.field
    [:div.ui.left.icon.input
      [:i.user.icon]
      [input-text (r/cursor state [:email])
        {:name "email"
         :placeholder "E-mail address"
         :on-key-up #(handle-keyup % state)}]]])

(defn input-password [state]
  [:div.field
    [:div.ui.left.icon.input
      [:i.lock.icon]
      [input-text (r/cursor state [:password])
        {:type "password"
         :name "password"
         :placeholder "Password"
         :on-key-up #(handle-keyup % state)}]]])

(defn btn-submit [state]
  [:div.ui.fluid.large.teal.submit.button
    {:on-click #(handle-login state)}
    "Login"])

(defn login-page []
  (let [state (r/atom {:email ""
                       :password ""})]
    (fn []
      [:div.ui.middle.aligned.center.aligned.grid.main
        [:div.column
          [:h2.ui.teal.image.header
            [:div.content "Login to your account"]]
          [:form.ui.large.form.error
            [:div.ui.stacked.segment
              [input-username state]
              [input-password state]
              [btn-submit state]]
            [:div.ui.error.message]]]])))
