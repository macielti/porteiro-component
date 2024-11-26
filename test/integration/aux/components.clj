(ns aux.components
  (:require [common-clj.integrant-components.config :as component.config]
            [common-clj.integrant-components.routes :as component.routes]
            [common-test-clj.component.postgresql-mock :as component.postgresql-mock]
            [common-test-clj.component.sqlite-mock :as component.sqlite-mock]
            [integrant.core :as ig]
            [porteiro-component.admin-component :as component.admin]
            [porteiro-component.diplomat.http-server :as porteiro.diplomat.http-server]
            [service-component.core :as component.service]))

(def schemas ["CREATE TABLE customers (id TEXT PRIMARY KEY, username TEXT NOT NULL, name TEXT, hashed_password TEXT NOT NULL);"
              "CREATE TABLE roles (id TEXT PRIMARY KEY, customer_id TEXT NOT NULL, role TEXT NOT NULL);"])

(def components-system-sqlite
  {::component.config/config           {:path "test/resources/config.example.edn"
                                        :env  :test}
   ::component.sqlite-mock/sqlite-mock {:schemas schemas}
   ::component.admin/admin             {:components {:config (ig/ref ::component.config/config)
                                                     :sqlite (ig/ref ::component.sqlite-mock/sqlite-mock)}}
   ::component.routes/routes           {:routes porteiro.diplomat.http-server/routes}
   ::component.service/service         {:components {:config (ig/ref ::component.config/config)
                                                     :routes (ig/ref ::component.routes/routes)
                                                     :sqlite (ig/ref ::component.sqlite-mock/sqlite-mock)}}})

(def components-system-postgresql
  {::component.config/config                   {:path "test/resources/config.example.edn"
                                                :env  :test}
   ::component.postgresql-mock/postgresql-mock {}
   ::component.admin/admin                     {:components {:config     (ig/ref ::component.config/config)
                                                             :postgresql (ig/ref ::component.postgresql-mock/postgresql-mock)}}
   ::component.routes/routes                   {:routes porteiro.diplomat.http-server/routes}
   ::component.service/service                 {:components {:config     (ig/ref ::component.config/config)
                                                             :routes     (ig/ref ::component.routes/routes)
                                                             :postgresql (ig/ref ::component.postgresql-mock/postgresql-mock)}}})
