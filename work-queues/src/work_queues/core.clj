(ns work-queues.core
  (:gen-class)
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb]
            [clojure.string    :as s]))

(defn -main
  [& args]
  (with-open [conn (rmq/connect)]
    (let [ch    (lch/open conn)
          qname "task_queue"
          payload (if (empty? args)
                    "Hello, world!"
                    (s/join " " args))]
      (lq/declare ch qname {:durable true :auto-delete false})
      (lb/publish ch "" qname payload {:persistent true})
      (println (format " [x] Sent %s" payload)))))