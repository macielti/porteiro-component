(ns porteiro-component.adapters.role
  (:require [camel-snake-kebab.core :as camel-snake-kebab]
            [porteiro-component.models.role :as models.role]
            [schema.core :as s])
  (:import (java.util UUID)))

(s/defn sqlite->internal :- models.role/Role
  [{:roles/keys [id customer_id role]}]
  {:role/id          (UUID/fromString id)
   :role/customer-id (UUID/fromString customer_id)
   :role/role        (camel-snake-kebab/->kebab-case-keyword role)})
