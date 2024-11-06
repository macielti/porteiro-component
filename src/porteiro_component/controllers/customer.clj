(ns porteiro-component.controllers.customer
  (:require [buddy.hashers :as hashers]
            [porteiro-component.adapters.customers :as adapters.customer]
            [porteiro-component.models.customer :as models.customer]
            [buddy.sign.jwt :as jwt]
            [java-time.api :as jt]
            [porteiro-component.db.postgresql.customer :as postgresql.customer]
            [common-clj.error.core :as common-error]
            [schema.core :as s]
            [pg.pool :as pool]))

(s/defn create-customer! :- models.customer/Customer
  [customer :- models.customer/Customer
   postgresql]
  (pool/with-connection [conn postgresql]
    (postgresql.customer/insert! customer conn)))

(s/defn ->token :- s/Str
  [map :- {s/Keyword s/Any}
   jwt-secret :- s/Str]
  (jwt/sign map jwt-secret {:exp (-> (jt/local-date-time (jt/zone-id "UTC"))
                                     (jt/plus (jt/days 1))
                                     (jt/sql-timestamp))}))

(s/defn authenticate-customer! :- s/Str
  [{:keys [username password]} :- models.customer/CustomerAuthentication
   {:keys [jwt-secret]}
   postgresql]
  (let [{:customer/keys [hashed-password] :as customer} (pool/with-connection [conn postgresql]
                                                          (postgresql.customer/by-username username conn))]
    (if (and customer (:valid (hashers/verify password hashed-password)))
      (-> {:customer (adapters.customer/internal->wire customer)}
          (->token jwt-secret))
      (common-error/http-friendly-exception 403
                                            "invalid-credentials"
                                            "Wrong username or/and password"
                                            "Customer is trying to login using invalid credentials"))))

(s/defn add-role!
  [customer-id :- s/Uuid
   role :- s/Keyword
   postgresql]
  (pool/with-connection [conn postgresql]
    (if (postgresql.customer/lookup customer-id conn)
      (postgresql.customer/add-role! customer-id role conn)
      (throw (ex-info "Customer not found"
                      {:status 404
                       :cause  "Customer not found"})))))
