(ns blab-stuff.core
  (:gen-class)
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.exchange  :as le]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb]))

(defn start-consumer
  "Starts a consumer bound to the given topic exchange in a separate thread"
  [ch topic-name username]
  (let [queue-name (format "github.gitfeeds.%s" username)
        handler    (fn [ch {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
                     (println (format "[consumer] %s received %s" username (String. payload "UTF-8"))))]
    (lq/declare ch queue-name {:exclusive false :auto-delete true})
    (lq/bind    ch queue-name topic-name)
    (lc/subscribe ch queue-name handler {:auto-ack true})))

(defn -main
  [& args]
  (let [conn  (rmq/connect)
        ch    (lch/open conn)
        ex    "github.requests"
        users ["xXDeadWingsXx" "truHealth    " "bo999287     "]]
    (le/declare ch ex "fanout" {:durable false :auto-delete true})
    (doseq [u users]
      (start-consumer ch ex u))
    (lb/publish ch ex "" "Pull Request made for nuance.io" {:content-type "text/plain" :type "scores.update"})
    (lb/publish ch ex "" "Commit made on devlop branch"  {:content-type "text/plain" :type "scores.update"})
    (Thread/sleep 2000)
    (rmq/close ch)
    (rmq/close conn)))