(ns porteiro-component.db.sqlite.customer
  (:require [next.jdbc :as jdbc]
            [porteiro-component.adapters.customers :as adapters.customer]
            [porteiro-component.adapters.role :as adapters.role]
            [porteiro-component.db.customer :as database.customer]
            [porteiro-component.models.customer :as models.customer]
            [porteiro-component.models.role :as models.role]
            [schema.core :as s])
  (:import (java.util UUID)))

(s/defn ^:private insert-role! :- models.role/Role
  [role :- s/Keyword
   customer-id :- s/Uuid
   database-conn]
  (-> (jdbc/execute! database-conn
                     ["INSERT INTO roles (id, customer_id, role) VALUES (?, ?, ?) RETURNING *;"
                      (str (random-uuid)) (str customer-id) (name role)])
      first
      adapters.role/sqlite->internal))

(s/defn ^:private roles-by-customer-id :- [models.role/Role]
  [customer-id :- s/Uuid
   database-conn]
  (->> (jdbc/execute! database-conn
                      ["SELECT * FROM roles WHERE customer_id = $1" (str customer-id)])
       (mapv adapters.role/sqlite->internal)))

(s/defmethod database.customer/insert! :sqlite :- models.customer/Customer
  [customer :- models.customer/Customer
   database]
  (jdbc/with-transaction [tx database]
    (-> (jdbc/execute! tx
                       ["INSERT INTO customers (id, username, name, hashed_password) VALUES (?, ?, ?, ?) RETURNING *;"
                        (:customer/id customer) (:customer/username customer) (:customer/name customer) (:customer/hashed-password customer)])
        first
        (adapters.customer/sqlite->internal
         (mapv #(insert-role! % (:customer/id customer) tx) (:customer/roles customer))))))

(s/defmethod database.customer/by-username :sqlite :- (s/maybe models.customer/Customer)
  [username :- s/Str
   database]
  (with-open [conn (jdbc/get-connection database)]
    (let [customer (-> (jdbc/execute! conn ["SELECT * FROM customers WHERE username = $1" username]) first)]
      (when customer
        (->> (roles-by-customer-id (UUID/fromString (:customers/id customer)) conn)
             (adapters.customer/sqlite->internal customer))))))

(s/defmethod database.customer/lookup :sqlite :- (s/maybe models.customer/Customer)
  [customer-id :- s/Uuid
   database]
  (with-open [conn (jdbc/get-connection database)]
    (let [customer (-> (jdbc/execute! conn ["SELECT * FROM customers WHERE id = $1" (str customer-id)]) first)
          roles (roles-by-customer-id (UUID/fromString (:customers/id customer)) conn)]
      (adapters.customer/sqlite->internal customer roles))))

(s/defmethod database.customer/add-role! :sqlite :- (s/maybe models.customer/Customer)
  [customer-id :- s/Uuid
   role :- s/Keyword
   database]
  (with-open [conn (jdbc/get-connection database)]
    (insert-role! role customer-id conn)
    (database.customer/lookup customer-id database)))

