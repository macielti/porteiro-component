(ns porteiro-component.db.postgresql.customer-test
  (:require [clojure.test :refer [is testing]]
            [common-test-clj.component.postgresql-mock :as component.postgresql-mock]
            [fixtures.customer]
            [matcher-combinators.test :refer [match?]]
            [porteiro-component.db.customer :as database.customer]
            [porteiro-component.db.postgresql.customer]
            [schema.test :as s]))

(s/deftest insert-test
  (testing "Should insert a customer"
    (let [pool (component.postgresql-mock/postgresql-pool-mock)]
      (is (= {:customer/id              fixtures.customer/customer-id
              :customer/username        fixtures.customer/customer-username
              :customer/name            fixtures.customer/customer-name
              :customer/roles           []
              :customer/hashed-password fixtures.customer/customer-hashed-password}
             (database.customer/insert! fixtures.customer/customer pool)))))

  (testing "Should insert a customer"
    (let [pool (component.postgresql-mock/postgresql-pool-mock)]
      (is (= {:customer/id              fixtures.customer/customer-id
              :customer/username        fixtures.customer/customer-username
              :customer/name            fixtures.customer/customer-name
              :customer/roles           [:admin]
              :customer/hashed-password fixtures.customer/customer-hashed-password}
             (database.customer/insert! (assoc fixtures.customer/customer :customer/roles [:admin]) pool))))))

(s/deftest by-username-test
  (testing "Should be able to query customer by its username"
    (let [pool (component.postgresql-mock/postgresql-pool-mock)]
      (is (match? {:customer/id fixtures.customer/customer-id}
                  (database.customer/insert! fixtures.customer/customer pool)))

      (is (match? {:customer/id fixtures.customer/customer-id}
                  (database.customer/by-username fixtures.customer/customer-username pool)))))

  (testing "Should be able to query customer by its username (customer with roles)"
    (let [pool (component.postgresql-mock/postgresql-pool-mock)]
      (is (match? {:customer/id fixtures.customer/customer-id}
                  (database.customer/insert! (assoc fixtures.customer/customer :customer/roles [:admin]) pool)))

      (is (match? {:customer/id    fixtures.customer/customer-id
                   :customer/roles [:admin]}
                  (database.customer/by-username fixtures.customer/customer-username pool))))))

(s/deftest lookup-test
  (testing "Should be able to query customer by its id"
    (let [pool (component.postgresql-mock/postgresql-pool-mock)]
      (is (match? {:customer/id fixtures.customer/customer-id}
                  (database.customer/insert! fixtures.customer/customer pool)))

      (is (match? {:customer/id fixtures.customer/customer-id}
                  (database.customer/lookup fixtures.customer/customer-id pool)))))

  (testing "Should be able to query customer by its id (customer with roles)"
    (let [pool (component.postgresql-mock/postgresql-pool-mock)]
      (is (match? {:customer/id fixtures.customer/customer-id}
                  (database.customer/insert! (assoc fixtures.customer/customer :customer/roles [:admin]) pool)))

      (is (match? {:customer/id    fixtures.customer/customer-id
                   :customer/roles [:admin]}
                  (database.customer/lookup fixtures.customer/customer-id pool))))))

(s/deftest add-role-test
  (testing "Should be able to query customer by its id"
    (let [pool (component.postgresql-mock/postgresql-pool-mock)]
      (is (match? {:customer/id    fixtures.customer/customer-id
                   :customer/roles []}
                  (database.customer/insert! fixtures.customer/customer pool)))

      (is (match? {:customer/id    fixtures.customer/customer-id
                   :customer/roles [:admin]}
                  (database.customer/add-role! fixtures.customer/customer-id :admin pool)))

      (is (match? {:customer/id    fixtures.customer/customer-id
                   :customer/roles [:admin :test]}
                  (database.customer/add-role! fixtures.customer/customer-id :test pool))))))
