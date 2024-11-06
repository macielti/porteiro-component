(ns porteiro-component.diplomat.http-server
  (:require [common-clj.io.interceptors :as io.interceptors]
            [common-clj.io.interceptors.customer :as io.interceptors.customer]
            [porteiro-component.diplomat.http-server.customer :as diplomat.http-server.customer]
            [porteiro-component.interceptors :as interceptors]
            [porteiro-component.wire.in.customer :as wire.in.customer]))

(def routes
  [["/api/customers" :post [(io.interceptors/schema-body-in-interceptor wire.in.customer/CustomerCreationDocument)
                            interceptors/username-already-in-use-interceptor
                            diplomat.http-server.customer/create-customer!] :route-name :create-customer]

   ["/api/customers/auth" :post [(io.interceptors/schema-body-in-interceptor wire.in.customer/CustomerAuthenticationDocument)
                                 diplomat.http-server.customer/authenticate-customer!] :route-name :customer-authentication]

   ["/api/customers/roles" :post [io.interceptors.customer/identity-interceptor
                                  (io.interceptors.customer/required-roles-interceptor [:admin])
                                  diplomat.http-server.customer/add-role!] :route-name :add-role-to-customer]])
