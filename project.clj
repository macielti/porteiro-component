(defproject net.clojars.macielti/porteiro-component "0.5.2"

  :description "Porteiro Component"

  :url "https://github.com/macielti/porteiro-component"

  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}

  :dependencies [[org.clojure/clojure "1.12.0"]
                 [prismatic/schema "1.4.1"]
                 [buddy/buddy-hashers "2.0.167"]
                 [dev.weavejester/medley "1.8.1"]
                 [camel-snake-kebab "0.4.3"]
                 [com.github.igrishaev/pg2-core "0.1.21"]
                 [buddy/buddy-sign "3.6.1-359"]
                 [clojure.java-time "1.4.3"]
                 [org.clojure/tools.logging "1.3.0"]
                 [net.clojars.macielti/service-component "2.4.2"]
                 [net.clojars.macielti/common-clj "41.74.73"]

                 [org.xerial/sqlite-jdbc "3.41.2.1"]
                 [com.github.seancorfield/next.jdbc "1.3.955"]]

  :profiles {:dev {:resource-paths ^:replace ["test/resources"]

                   :test-paths     ^:replace ["test/unit" "test/integration" "test/helpers"]

                   :plugins        [[com.github.clojure-lsp/lein-clojure-lsp "1.4.15"]
                                    [com.github.liquidz/antq "RELEASE"]
                                    [migratus-lein "0.7.3"]
                                    [lein-cloverage "1.2.4"]]

                   :dependencies   [[net.clojars.macielti/common-test-clj "4.2.3"]
                                    [migratus "1.6.3"]
                                    [danlentz/clj-uuid "0.2.0"]
                                    [nubank/matcher-combinators "3.9.1"]
                                    [cheshire "5.13.0"]
                                    [hashp "0.2.2"]]

                   :migratus       {:store         :database
                                    :migration-dir "migrations-sqlite"}

                   :injections     [(require 'hashp.core)]

                   :aliases        {"clean-ns"     ["clojure-lsp" "clean-ns" "--dry"] ;; check if namespaces are clean
                                    "format"       ["clojure-lsp" "format" "--dry"] ;; check if namespaces are formatted
                                    "diagnostics"  ["clojure-lsp" "diagnostics"]
                                    "lint"         ["do" ["clean-ns"] ["format"] ["diagnostics"]]
                                    "clean-ns-fix" ["clojure-lsp" "clean-ns"]
                                    "format-fix"   ["clojure-lsp" "format"]
                                    "lint-fix"     ["do" ["clean-ns-fix"] ["format-fix"]]}}}
  :resource-paths ["resources"])
