(ns aux.http
  (:require [cheshire.core :as json]
            [io.pedestal.test :as test]))

(defn create-customer!
  [customer
   service-fn]
  (let [{:keys [body status]} (test/response-for service-fn
                                                 :post "/api/customers"
                                                 :headers {"Content-Type" "application/json"}
                                                 :body (json/encode customer))]
    {:status status
     :body   (json/decode body true)}))

(defn authenticate-customer
  [credentials
   service-fn]
  (let [{:keys [body status]} (test/response-for service-fn
                                                 :post "/api/customers/auth"
                                                 :headers {"Content-Type" "application/json"}
                                                 :body (json/encode credentials))]
    {:status status
     :body   (json/decode body true)}))

(defn add-role-to-customer!
  [customer-id
   role
   token
   service-fn]
  (let [{:keys [body status]} (test/response-for service-fn
                                                 :post (str "/api/customers/roles?customer-id=" customer-id "&role=" (name role))
                                                 :headers {"Content-Type"  "application/json"
                                                           "Authorization" (str "Bearer " token)}
                                                 :body (json/encode {}))]
    {:status status
     :body   (json/decode body true)}))

