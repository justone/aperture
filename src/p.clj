(ns p
  (:require [portal.api :as portal]))

(defonce inspector (atom nil))

(defn open
  [& [opts]]
  (if-let [p (:portal (deref inspector))]
    (portal/open p)
    (let [p (portal/open opts)]
      (add-tap #'portal/submit)
      (reset! inspector {:portal p :opts opts}))))

(defn close
  []
  (when-let [p (:portal (deref inspector))]
    (remove-tap #'portal/submit)
    (portal/close p)
    (reset! inspector nil)))

(defn v
  []
  (some-> inspector deref :portal deref))

(defn s
  []
  (some-> inspector deref))

(defn c
  []
  (some-> inspector deref :portal (portal/clear)))

(defonce shutdown
  (do
    (println "adding portal shutdown")
    (.addShutdownHook
    (Runtime/getRuntime)
    (Thread. #(portal.api/close)))))

(comment
  (open)
  (s)
  (v)
  (c)
  (close)
  (deref inspector)
  (tap> {:foo [(range 5)]})
  )
