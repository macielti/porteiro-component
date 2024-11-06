(ns porteiro-component.admin-component
  (:require [integrant.core :as ig]
            [pg.pool :as pool]
            [porteiro-component.db.postgresql.customer :as postgresql.customer]
            [porteiro-component.diplomat.http-server.customer :as diplomat.http-server.customer]
            [taoensso.timbre :as log]))

(defmethod ig/init-key ::admin
  [_ {:keys [components]}]
  (log/info :starting ::admin)
  (let [{:keys [admin-customer-seed]} (:config components)]
    (when-not (pool/with-connection [database-conn (:postgresql components)]
                (postgresql.customer/by-username (get-in admin-customer-seed [:customer :username]) database-conn))
      (let [wire-customer-id (-> (diplomat.http-server.customer/create-customer! {:json-params admin-customer-seed
                                                                                  :components  components})
                                 (get-in [:body :customer :id]))]
        (diplomat.http-server.customer/add-role! {:query-params {:customer-id wire-customer-id
                                                                 :role        "ADMIN"}
                                                  :components   components})))))

(defmethod ig/halt-key! ::admin
  [_ _]
  (log/info :stopping ::admin))
