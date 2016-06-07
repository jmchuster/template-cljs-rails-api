(ns web.components.footer
  (:require [reagent.core :as r]
            [cljs-time.core :refer [year now]]))

(defn footer []
  [:div.ui.inverted.vertical.footer.segment
    [:div.ui.center.aligned.container
      [:div.small
        [:i.copyright.icon]
        [:span (year (now)) " My_App, Inc."]]]])
