(ns routing.emit-direct
  (:gen-class)
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.exchange  :as le]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb]
            [clojure.string    :as s]))

(def ^{:const true} x "direct_logs")

(defn -main
  [severity & args]
  (with-open [conn (rmq/connect)]
    (let [ch    (lch/open conn)
          severity (or severity "info")
          payload (if (empty? args)
                    "Hello, world!"
                    (s/join " " args))]
      (le/direct ch x {:durable false :auto-delete false})
      (lb/publish ch x severity payload)
      (println (format " [x] Sent %s" payload)))))