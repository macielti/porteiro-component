(ns postgresql.customer-authentication-test
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
        service-fn (-> system ::component.service/service :io.pedestal.http/service-fn)]

    (testing "Create Customer"
      (is (match? {:status 201
                   :body   {:customer {:id       clj-uuid/uuid-string?
                                       :roles    []
                                       :username "test"}}}
                  (http/create-customer! {:customer {:username "test"
                                                     :password "password"}} service-fn))))

    (testing "Authenticate Customer"
      (is (match? {:status 200
                   :body   {:token string?}}
                  (http/authenticate-customer {:customer {:username "test"
                                                          :password "password"}} service-fn))))

    (ig/halt! system)))
