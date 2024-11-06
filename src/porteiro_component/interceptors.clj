(ns porteiro-component.interceptors
  (:require [common-clj.error.core :as common-error]
            [pg.pool :as pool]
            [porteiro-component.db.postgresql.customer :as postgresql.customer]))

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
