(ns porteiro-component.interceptors.customer
  (:require [porteiro-component.db.customer :as database.customer]
            [porteiro-component.db.postgresql.customer]
            [porteiro-component.db.sqlite.customer]
            [service-component.error :as common-error]))

(def username-already-in-use-interceptor
  {:name  ::username-already-in-use-interceptor
   :enter (fn [{{json-params                 :json-params
                 {:keys [postgresql sqlite]} :components} :request :as context}]
            (let [username (get-in json-params [:customer :username] "")
                  customer (database.customer/by-username username (or postgresql sqlite))]
              (when-not (empty? customer)
                (common-error/http-friendly-exception 409
                                                      "not-unique"
                                                      "Username already in use"
                                                      "username already in use by another customer")))
            context)})
