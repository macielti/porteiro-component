(ns postgresql.customer-roles-test
  (:require [aux.components :as components]
            [aux.http :as http]
            [clj-uuid]
            [clojure.test :refer [is testing]]
            [integrant.core :as ig]
            [matcher-combinators.test :refer [match?]]
            [schema.test :as s]
            [service-component.core :as component.service]))

(s/deftest authenticate-customer-test
  (let [system (ig/init components/components-system-postgresql)
        service-fn (-> system ::component.service/service :io.pedestal.http/service-fn)
        customer-id (-> (http/create-customer! {:customer {:username "test"
                                                           :password "password"}} service-fn) :body :customer :id)
        {:keys [token]} (-> (http/authenticate-customer {:customer {:username "admin"
                                                                    :password "da3bf409"}} service-fn) :body)]

    (testing "Authenticate Customer"
      (is (string? token)))

    (testing "Add role to Customer"
      (is (match? {:status 200
                   :body   {:id       clj-uuid/uuid-string?
                            :username "test"
                            :roles    ["admin"]}}
                  (http/add-role-to-customer! customer-id :admin token service-fn))))

    (ig/halt! system)))
