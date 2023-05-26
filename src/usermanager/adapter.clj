(ns usermanager.adapter
  (:require [next.jdbc :as jdbc]
            [ring.adapter.jetty :refer [run-jetty]]
            [usermanager.handler :as handler]
            [usermanager.model.user-manager :refer [populate]]
            [integrant.core :as ig]))

(defmethod ig/init-key :adapter/jetty [_ {:keys [handler] :as opts}]
  (run-jetty handler (-> opts (dissoc handler) (assoc :join? false))))

(defmethod ig/init-key :handler/run-app [_ {:keys [db]}]
  (handler/app db))

(defmethod ig/init-key :database.sql/connection [_ db-spec]
  (let [conn (jdbc/get-datasource db-spec)]
    (populate conn (:dbtype db-spec))
    conn))

(defmethod ig/halt-key! :adapter/jetty [_ server]
  (.stop server))
