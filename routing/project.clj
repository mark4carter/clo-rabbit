(defproject my-stuff "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
  								[com.novemberain/langohr "3.3.0"]
  								[clj-http "2.0.0"]]
  :profiles {:uberjar {:aot :all}})
