(ns porteiro-component.db.customer
  (:require [porteiro-component.models.customer :as models.customer]
            [schema.core :as s])
  (:import (org.pg Pool)
           (org.sqlite.jdbc4 JDBC4Connection)))

;TODO: We look at the database type class to decide which database to use
(s/defn ^:private insert-database-type-dispatcher
  [_customer :- models.customer/Customer
   database :- s/Any]
  (cond
    (= (type database) Pool) :postgresql
    (= (type database) JDBC4Connection) :sqlite))

(defmulti insert! insert-database-type-dispatcher)

(s/defn ^:private by-username-database-type-dispatcher
  [_username :- s/Str
   database :- s/Any]
  (cond
    (= (type database) Pool) :postgresql
    (= (type database) JDBC4Connection) :sqlite))

(defmulti by-username by-username-database-type-dispatcher)

(s/defn ^:private lookup-database-type-dispatcher
  [_customer-id :- s/Uuid
   database :- s/Any]
  (cond
    (= (type database) Pool) :postgresql
    (= (type database) JDBC4Connection) :sqlite))

(defmulti lookup lookup-database-type-dispatcher)

(s/defn ^:private add-role-database-type-dispatcher
  [_customer-id :- s/Uuid
   _role :- s/Keyword
   database :- s/Any]
  (cond
    (= (type database) Pool) :postgresql
    (= (type database) JDBC4Connection) :sqlite))

(defmulti add-role! add-role-database-type-dispatcher)
