(ns topics.emit-topic
  (:gen-class)
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.exchange  :as le]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb]
            [clojure.string    :as s]))

(def ^{:const true} xchange "topic_logs")

(defn -main
  [severity & args]
  (with-open [conn (rmq/connect)]
    (let [ch    (lch/open conn)
          severity (or severity "anonymous.info")
          payload (if (empty? args)
                    "Hello, world!"
                    (s/join " " args))]
      (le/topic ch xchange {:durable false :auto-delete false})
      (lb/publish ch xchange severity payload)
      (println (format " [x] Sent %s" payload)))))