(ns porteiro-component.admin-component
  (:require [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [porteiro-component.db.customer :as database.customer]
            [porteiro-component.db.postgresql.customer]
            [porteiro-component.db.sqlite.customer]
            [porteiro-component.diplomat.http-server.customer :as diplomat.http-server.customer]))

(defmethod ig/init-key ::admin
  [_ {:keys [components]}]
  (log/info :starting ::admin)
  (let [{:keys [admin-customer-seed]} (:config components)
        database (or (:postgresql components)
                     (:sqlite components))]
    (when-not (database.customer/by-username (get-in admin-customer-seed [:customer :username]) database)
      (let [wire-customer-id (-> (diplomat.http-server.customer/create-customer! {:json-params admin-customer-seed
                                                                                  :components  components})
                                 (get-in [:body :customer :id]))]
        (diplomat.http-server.customer/add-role! {:query-params {:customer-id wire-customer-id
                                                                 :role        "ADMIN"}
                                                  :components   components})))))

(defmethod ig/halt-key! ::admin
  [_ _]
  (log/info :stopping ::admin))
