(ns web.controls
  (:require [reagent.core :as r]
            [web.session :as session]
            [web.utils :as utils]
            [ajax.core :refer [GET POST ajax-request]]))

(defn login-user! [username password]
  (POST "/api/sessions"
    {:params {:username username
              :password password}
     :format :json
     :response-format :json
     :keywords? true
     :handler
      (fn [response]
        (.log js/console (str response))
        (session/set-user! (:data response))
        (if (= (utils/current_url) "/login") (utils/redirect_url! "/")))
     :error-handler
      (fn [{response :response} result]
        (.log js/console (str response))
        (js/alert (:detail (nth (:errors response) 0))))}))

(defn logout-user! []
  (session/set-user! nil))

(defn render-page! [page-fn]
  (session/set-page! page-fn))
