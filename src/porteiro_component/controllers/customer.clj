(ns porteiro-component.controllers.customer
  (:require [buddy.hashers :as hashers]
            [buddy.sign.jwt :as jwt]
            [java-time.api :as jt]
            [porteiro-component.adapters.customers :as adapters.customer]
            [porteiro-component.db.customer :as database.customer]
            [porteiro-component.db.postgresql.customer]
            [porteiro-component.db.sqlite.customer]
            [porteiro-component.models.customer :as models.customer]
            [schema.core :as s]
            [service-component.error :as common-error]))

(s/defn create-customer! :- models.customer/Customer
  [customer :- models.customer/Customer
   database]
  (database.customer/insert! customer database))

(s/defn ->token :- s/Str
  [map :- {s/Keyword s/Any}
   jwt-secret :- s/Str]
  (jwt/sign map jwt-secret {:exp (-> (jt/local-date-time (jt/zone-id "UTC"))
                                     (jt/plus (jt/days 1))
                                     (jt/sql-timestamp))}))

(s/defn authenticate-customer! :- s/Str
  [{:keys [username password]} :- models.customer/CustomerAuthentication
   {:keys [jwt-secret]}
   database]
  (let [{:customer/keys [hashed-password] :as customer} (database.customer/by-username username database)]
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
   database]
  (if (database.customer/lookup customer-id database)
    (database.customer/add-role! customer-id role database)
    (common-error/http-friendly-exception 404 "customer-not-found" "" "")))
