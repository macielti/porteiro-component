(ns porteiro-component.db.postgresql.customer
  (:require [pg.core :as pg]
            [pg.pool :as pool]
            [porteiro-component.adapters.customers :as adapters.customer]
            [porteiro-component.db.customer :as database.customer]
            [porteiro-component.models.customer :as models.customer]
            [schema.core :as s]))

(s/defmethod database.customer/insert! :postgresql :- models.customer/Customer
  [{:customer/keys [id username roles hashed-password] :as customer} :- models.customer/Customer
   postgresql]
  (pool/with-connection [conn postgresql]
    (-> (pg/execute conn
                    "INSERT INTO customers (id, username, name, roles, hashed_password) VALUES ($1, $2, $3, $4, $5)
                    returning *"
                    {:params [id username (:customer/name customer) (mapv name roles) hashed-password]})
        first
        adapters.customer/postgresql->internal)))

(s/defmethod database.customer/by-username :postgresql :- (s/maybe models.customer/Customer)
  [username :- s/Str
   postgresql]
  (pool/with-connection [conn postgresql]
    (some-> (pg/execute conn
                        "SELECT * FROM customers WHERE username = $1"
                        {:params [username]})
            first
            adapters.customer/postgresql->internal)))

(s/defmethod database.customer/lookup :postgresql :- (s/maybe models.customer/Customer)
  [customer-id :- s/Uuid
   postgresql]
  (pool/with-connection [conn postgresql]
    (some-> (pg/execute conn
                        "SELECT * FROM customers WHERE id = $1"
                        {:params [customer-id]})
            first
            adapters.customer/postgresql->internal)))

(s/defmethod database.customer/add-role! :postgresql :- (s/maybe models.customer/Customer)
  [customer-id :- s/Uuid
   role :- s/Keyword
   postgresql]
  (pool/with-connection [conn postgresql]
    (some-> (pg/execute conn
                        "UPDATE customers SET roles = array_append(roles, $1) WHERE id = $2 returning *"
                        {:params [(adapters.customer/internal-role->wire-role role) customer-id]})
            first
            adapters.customer/postgresql->internal)))
