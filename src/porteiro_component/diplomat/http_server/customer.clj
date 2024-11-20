(ns porteiro-component.diplomat.http-server.customer
  (:require [porteiro-component.adapters.customers :as adapters.customer]
            [porteiro-component.controllers.customer :as controllers.customer]
            [schema.core :as s])
  (:import (java.util UUID)))

(s/defn create-customer!
  [{{:keys [customer]}          :json-params
    {:keys [postgresql sqlite]} :components}]
  {:status 201
   :body   {:customer (-> (adapters.customer/wire->internal customer)
                          (controllers.customer/create-customer! (or postgresql sqlite))
                          adapters.customer/internal->wire)}})

(s/defn authenticate-customer!
  [{{:keys [customer]}                 :json-params
    {:keys [postgresql sqlite config]} :components}]
  {:status 200
   :body   (-> (adapters.customer/wire->internal-customer-authentication customer)
               (controllers.customer/authenticate-customer! config (or postgresql sqlite))
               adapters.customer/customer-token->wire)})

(s/defn add-role!
  [{{wire-customer-id :customer-id
     wire-role        :role}    :query-params
    {:keys [postgresql sqlite]} :components}]
  {:status 200
   :body   (-> (UUID/fromString wire-customer-id)
               (controllers.customer/add-role! (adapters.customer/wire->internal-role wire-role) (or postgresql sqlite))
               adapters.customer/internal->wire)})
