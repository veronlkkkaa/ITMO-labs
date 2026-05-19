(defproject lab1 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.2"]]
  :main ^:skip-aot lab1.core
  :target-path "target/%s"
  :java-source-paths ["src"]
  :javac-options ["-target" "17" "-source" "17"]
  :resource-paths ["classes"]
  :plugins [[lein-cljfmt "0.8.2"]
            [lein-kibit "0.1.8"]
            [lein-bikeshed "0.5.2"]]
  :prep-tasks ["javac" "compile"]
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
             :dev {:dependencies [[midje "1.10.9"]
                                  [org.clojure/test.check "1.1.1"]
                                  [pjstadig/humane-test-output "0.11.0"]
                                  [clj-kondo "2023.10.20"]]}}
  :test-selectors {:default (complement :integration)
                   :integration :integration
                   :all (constantly true)}
  :aliases {"lint" ["do"
                    ["cljfmt" "check"]
                    ["kibit"]
                    ["bikeshed" "--max-line-length" "120"]
                    ["run" "-m" "clj-kondo.main" "--lint" "src" "test"]]})