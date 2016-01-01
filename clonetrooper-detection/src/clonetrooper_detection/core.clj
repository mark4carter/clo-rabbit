(ns clonetrooper-detection.core
  (:gen-class)
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.exchange  :as le]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb]))

(def ^{:const true}
  clone-exchange "weathr")

(defn start-consumer
  "Starts a consumer bound to the given topic exchange in a separate thread"
  [ch topic-name queue-name]
  (let [queue-name' (:queue (lq/declare ch queue-name {:exclusive false :auto-delete true}))
        handler     (fn [ch {:keys [routing-key] :as meta} ^bytes payload]
                      (println (format "'%s' has been updated from %s, routing key: %s" (String. payload "UTF-8") queue-name' routing-key)))]
    (lq/bind    ch queue-name' clone-exchange {:routing-key topic-name})
    (lc/subscribe ch queue-name' handler {:auto-ack true})))

(defn publish-update
  "Publishes a detection update"
  [ch payload routing-key]
  (lb/publish ch clone-exchange routing-key payload {:content-type "text/plain" :type "clone.update"}))

(defn -main
  [& args]
  (let [conn      (rmq/connect)
        ch        (lch/open conn)
        locations {""               "outerrim.tantooine.*"
                   "outerrim.south" "outerrim.yavin4.#"
                   "moseisley.california"  "outerrim.tantooine.moseisley.*"
                   "huttpalace"   "#.huttpalace"
                   "yodas house"        "dagobah.yoda.#"
                   "naboo palace"        "naboo.palace"}]
    (le/declare ch clone-exchange "topic" {:durable false :auto-delete true})
    (doseq [[k v] locations]
      (start-consumer ch v k))
    (publish-update ch "Mos Eisley North" "outerrim.tantooine.moseisley.north.cantina")
    (Thread/sleep 1000)
    (publish-update ch "Mos Eisley South" "outerrim.tantooine.moseisley.south")
    (publish-update ch "Mos Eisley East"  "outerrim.tantooine.moseisley.east.cantina")
    (publish-update ch "Mos Eisley West"  "outerrim.tantooine.moseisley.west")    
    (Thread/sleep 2000)
    (publish-update ch "Jabba Hutt Palace"  "outerrim.tantooine.huttpalace")
    (publish-update ch "Yavin4 Forest" "outerrim.yavin4.south.rainforest")
    (publish-update ch "Yoda Update" "dagobah.yoda")
    (publish-update ch "Dagobah North"     "dagobah.north")
    (publish-update ch "Kashyyk"  "kashyyk")
    (publish-update ch "Naboo Palace"      "naboo.palace")
    (publish-update ch "Hoth"     "hoth.some.frozen.place")
    (Thread/sleep 2000)
    (rmq/close ch)
    (rmq/close conn)))
