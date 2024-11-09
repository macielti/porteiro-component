(ns porteiro-component.interceptors.customer
  (:require [pg.pool :as pool]
            [porteiro-component.db.postgresql.customer :as postgresql.customer]
            [service-component.error :as common-error]))

(def username-already-in-use-interceptor
  {:name  ::username-already-in-use-interceptor
   :enter (fn [{{json-params          :json-params
                 {:keys [postgresql]} :components} :request :as context}]
            (let [username (get-in json-params [:customer :username] "")
                  customer (pool/with-connection [database-conn postgresql]
                             (postgresql.customer/by-username username database-conn))]
              (when-not (empty? customer)
                (common-error/http-friendly-exception 409
                                                      "not-unique"
                                                      "Username already in use"
                                                      "username already in use by another customer")))
            context)})