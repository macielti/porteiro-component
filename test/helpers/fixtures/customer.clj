(ns fixtures.customer
  (:require [common-test-clj.helpers.schema :as helpers.schema]
            [porteiro-component.models.customer :as models.customer]
            [schema.core :as s]))

(def customer-id (random-uuid))
(def customer-username "random-username")
(def customer-name "Manuel")
(def customer-hashed-password "random-hash")

(s/def customer :- models.customer/Customer
  (helpers.schema/generate models.customer/Customer
                           {:customer/id              customer-id
                            :customer/username        customer-username
                            :customer/name            customer-name
                            :customer/hashed-password customer-hashed-password}))
