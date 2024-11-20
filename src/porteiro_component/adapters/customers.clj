(ns porteiro-component.adapters.customers
  (:require [buddy.hashers :as hashers]
            [camel-snake-kebab.core :as camel-snake-kebab]
            [medley.core :as medley]
            [porteiro-component.models.customer :as models.customer]
            [porteiro-component.models.role :as models.role]
            [porteiro-component.wire.in.customer :as wire.in.customer]
            [porteiro-component.wire.out.customer :as wire.out.customer]
            [schema.core :as s])
  (:import (java.util UUID)))

(s/defn wire->internal :- models.customer/Customer
  [{:keys [username password name]} :- wire.in.customer/Customer]
  (medley/assoc-some {:customer/id              (random-uuid)
                      :customer/username        username
                      :customer/hashed-password (hashers/derive password)}
                     :customer/name name))

(s/defn internal->wire :- wire.out.customer/Customer
  [{:customer/keys [id username roles name] :or {roles []}} :- models.customer/Customer]
  (medley/assoc-some {:id       (str id)
                      :username username
                      :roles    (map clojure.core/name roles)}
                     :name name))

(s/defn wire->internal-customer-authentication :- models.customer/CustomerAuthentication
  [{:keys [username password]} :- wire.in.customer/CustomerAuthentication]
  {:username username
   :password password})

(s/defn customer-token->wire :- wire.out.customer/CustomerToken
  [token :- s/Str]
  {:token token})

(s/defn wire->internal-role :- s/Keyword
  [wire-role :- s/Str]
  (camel-snake-kebab/->kebab-case-keyword wire-role))

(s/defn internal-role->wire-role :- s/Str
  [wire-role :- s/Keyword]
  (camel-snake-kebab/->snake_case_string wire-role))

(s/defn postgresql->internal :- models.customer/Customer
  [{:keys [id username roles name hashed_password]}]
  (medley/assoc-some {:customer/id              id
                      :customer/username        username
                      :customer/hashed-password hashed_password
                      :customer/roles           (map wire->internal-role roles)}
                     :customer/name name))

(s/defn sqlite->internal :- models.customer/Customer
  [{:customers/keys [id username name hashed_password]}
   roles :- [models.role/Role]]
  (medley/assoc-some {:customer/id              (UUID/fromString id)
                      :customer/username        username
                      :customer/hashed-password hashed_password
                      :customer/roles           (map :role/role roles)}
                     :customer/name name))
