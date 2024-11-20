(ns porteiro-component.models.role
  (:require [schema.core :as s]))

(def role
  {:role/id          s/Uuid
   :role/customer-id s/Uuid
   :role/role        s/Keyword})

(s/defschema Role
  role)
