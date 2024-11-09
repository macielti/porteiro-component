(ns porteiro-component.diplomat.http-server
  (:require [porteiro-component.diplomat.http-server.customer :as diplomat.http-server.customer]
            [porteiro-component.interceptors.authentication :as interceptors.authentication]
            [porteiro-component.interceptors.customer :as io.interceptors.customer]
            [porteiro-component.wire.in.customer :as wire.in.customer]
            [service-component.interceptors :as service-component.interceptors]))

(def routes
  [["/api/customers" :post [(service-component.interceptors/schema-body-in-interceptor wire.in.customer/CustomerCreationDocument)
                            io.interceptors.customer/username-already-in-use-interceptor
                            diplomat.http-server.customer/create-customer!] :route-name :create-customer]

   ["/api/customers/auth" :post [(service-component.interceptors/schema-body-in-interceptor wire.in.customer/CustomerAuthenticationDocument)
                                 diplomat.http-server.customer/authenticate-customer!] :route-name :customer-authentication]

   ["/api/customers/roles" :post [interceptors.authentication/identity-interceptor
                                  (interceptors.authentication/required-roles-interceptor [:admin])
                                  diplomat.http-server.customer/add-role!] :route-name :add-role-to-customer]])
