(ns usermanager.system
  (:require [integrant.core :as ig]
            [usermanager.adapter])
  (:gen-class))

(def config
  {:adapter/jetty {:handler (ig/ref :handler/run-app) :port 3000}
   :handler/run-app {:db (ig/ref :database.sql/connection)}
   :database.sql/connection {:dbtype "sqlite" :dbname "usermanager_db"}})

(defn -main []
  (ig/init config))