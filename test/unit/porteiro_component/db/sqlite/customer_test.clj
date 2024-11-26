(ns porteiro-component.db.sqlite.customer-test
  (:require [clojure.test :refer [is testing]]
            [common-test-clj.component.sqlite-mock :as component.sqlite-mock]
            [fixtures.customer]
            [aux.components]
            [matcher-combinators.test :refer [match?]]
            [porteiro-component.db.customer :as database.customer]
            [porteiro-component.db.sqlite.customer]
            [schema.test :as s]))

(s/deftest insert-sqlite-test
  (testing "That we are able to insert a customer into the database"
    (let [database-conn (component.sqlite-mock/sqlite-unit-mock aux.components/schemas)]
      (is (= {:customer/id              fixtures.customer/customer-id
              :customer/username        fixtures.customer/customer-username
              :customer/name            fixtures.customer/customer-name
              :customer/roles           []
              :customer/hashed-password fixtures.customer/customer-hashed-password}
             (database.customer/insert! fixtures.customer/customer database-conn)))))

  (testing "That we are able to insert a customer into the database (with roles)"
    (let [database-conn (component.sqlite-mock/sqlite-unit-mock aux.components/schemas)]
      (is (= {:customer/id              fixtures.customer/customer-id
              :customer/username        fixtures.customer/customer-username
              :customer/name            fixtures.customer/customer-name
              :customer/roles           [:admin]
              :customer/hashed-password fixtures.customer/customer-hashed-password}
             (database.customer/insert! (assoc fixtures.customer/customer :customer/roles [:admin]) database-conn))))))

(s/deftest by-username-test
  (testing "That we are able to query customers by its username"
    (let [database-conn (component.sqlite-mock/sqlite-unit-mock aux.components/schemas)]
      (is (match? {:customer/id fixtures.customer/customer-id}
                  (database.customer/insert! fixtures.customer/customer database-conn)))

      (is (match? {:customer/id fixtures.customer/customer-id}
                  (database.customer/by-username fixtures.customer/customer-username database-conn)))))

  (testing "That we are able to query customers by its username (with roles)"
    (let [database-conn (component.sqlite-mock/sqlite-unit-mock aux.components/schemas)]
      (is (match? {:customer/id fixtures.customer/customer-id}
                  (database.customer/insert! (assoc fixtures.customer/customer :customer/roles [:admin]) database-conn)))

      (is (match? {:customer/id    fixtures.customer/customer-id
                   :customer/roles [:admin]}
                  (database.customer/by-username fixtures.customer/customer-username database-conn))))))

(s/deftest lookup-test
  (testing "That we are able to query customers by its id"
    (let [database-conn (component.sqlite-mock/sqlite-unit-mock aux.components/schemas)]
      (is (match? {:customer/id fixtures.customer/customer-id}
                  (database.customer/insert! fixtures.customer/customer database-conn)))

      (is (match? {:customer/id fixtures.customer/customer-id}
                  (database.customer/lookup fixtures.customer/customer-id database-conn)))))

  (testing "That we are able to query customers by its id (with roles)"
    (let [database-conn (component.sqlite-mock/sqlite-unit-mock aux.components/schemas)]
      (is (match? {:customer/id fixtures.customer/customer-id}
                  (database.customer/insert! (assoc fixtures.customer/customer :customer/roles [:admin]) database-conn)))

      (is (match? {:customer/id    fixtures.customer/customer-id
                   :customer/roles [:admin]}
                  (database.customer/lookup fixtures.customer/customer-id database-conn))))))

(s/deftest add-role-test
  (testing "That we are able to add role to a customer"
    (let [database-conn (component.sqlite-mock/sqlite-unit-mock aux.components/schemas)]
      (is (match? {:customer/id fixtures.customer/customer-id}
                  (database.customer/insert! fixtures.customer/customer database-conn)))

      (is (match? {:customer/id    fixtures.customer/customer-id
                   :customer/roles [:admin]}
                  (database.customer/add-role! fixtures.customer/customer-id :admin database-conn)))

      (is (match? {:customer/id    fixtures.customer/customer-id
                   :customer/roles [:admin :test]}
                  (database.customer/add-role! fixtures.customer/customer-id :test database-conn))))))
