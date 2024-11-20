(ns porteiro-component.db.sqlite.customer-test
  (:require [clojure.test :refer [is testing]]
            [common-test-clj.component.sqlite-mock :as component.sqlite-mock]
            [fixtures.customer]
            [matcher-combinators.test :refer [match?]]
            [porteiro-component.db.customer :as database.customer]
            [porteiro-component.db.sqlite.customer]
            [schema.test :as s]))

(s/deftest insert-sqlite-test
  (testing "That we are able to insert a customer into the database"
    (let [database (component.sqlite-mock/sqlite-unit-mock)]
      (is (= {:customer/id              fixtures.customer/customer-id
              :customer/username        fixtures.customer/customer-username
              :customer/name            fixtures.customer/customer-name
              :customer/roles           []
              :customer/hashed-password fixtures.customer/customer-hashed-password}
             (database.customer/insert! fixtures.customer/customer database)))))

  (testing "That we are able to insert a customer into the database (with roles)"
    (let [database (component.sqlite-mock/sqlite-unit-mock)]
      (is (= {:customer/id              fixtures.customer/customer-id
              :customer/username        fixtures.customer/customer-username
              :customer/name            fixtures.customer/customer-name
              :customer/roles           [:admin]
              :customer/hashed-password fixtures.customer/customer-hashed-password}
             (database.customer/insert! (assoc fixtures.customer/customer :customer/roles [:admin]) database))))))

(s/deftest by-username-test
  (testing "That we are able to query customers by its username"
    (let [database (component.sqlite-mock/sqlite-unit-mock)]
      (is (match? {:customer/id fixtures.customer/customer-id}
                  (database.customer/insert! fixtures.customer/customer database)))

      (is (match? {:customer/id fixtures.customer/customer-id}
                  (database.customer/by-username fixtures.customer/customer-username database)))))

  (testing "That we are able to query customers by its username (with roles)"
    (let [database (component.sqlite-mock/sqlite-unit-mock)]
      (is (match? {:customer/id fixtures.customer/customer-id}
                  (database.customer/insert! (assoc fixtures.customer/customer :customer/roles [:admin]) database)))

      (is (match? {:customer/id    fixtures.customer/customer-id
                   :customer/roles [:admin]}
                  (database.customer/by-username fixtures.customer/customer-username database))))))

(s/deftest lookup-test
  (testing "That we are able to query customers by its id"
    (let [database (component.sqlite-mock/sqlite-unit-mock)]
      (is (match? {:customer/id fixtures.customer/customer-id}
                  (database.customer/insert! fixtures.customer/customer database)))

      (is (match? {:customer/id fixtures.customer/customer-id}
                  (database.customer/lookup fixtures.customer/customer-id database)))))

  (testing "That we are able to query customers by its id (with roles)"
    (let [database (component.sqlite-mock/sqlite-unit-mock)]
      (is (match? {:customer/id fixtures.customer/customer-id}
                  (database.customer/insert! (assoc fixtures.customer/customer :customer/roles [:admin]) database)))

      (is (match? {:customer/id    fixtures.customer/customer-id
                   :customer/roles [:admin]}
                  (database.customer/lookup fixtures.customer/customer-id database))))))

(s/deftest add-role-test
  (testing "That we are able to add role to a customer"
    (let [database (component.sqlite-mock/sqlite-unit-mock)]
      (is (match? {:customer/id fixtures.customer/customer-id}
                  (database.customer/insert! fixtures.customer/customer database)))

      (is (match? {:customer/id    fixtures.customer/customer-id
                   :customer/roles [:admin]}
                  (database.customer/add-role! fixtures.customer/customer-id :admin database)))

      (is (match? {:customer/id    fixtures.customer/customer-id
                   :customer/roles [:admin :test]}
                  (database.customer/add-role! fixtures.customer/customer-id :test database))))))
