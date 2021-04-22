(defproject ssss-expressions "0.1.11-SNAPSHOT"
  :description "Entry for the Spring Lisp Game Jam 2021"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [quip "1.0.6"]]
  :main ^:skip-aot ssss-expressions.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
