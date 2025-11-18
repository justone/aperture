(ns aperture.core
  (:require [portal.api :as portal]))

(defonce shutdown
  (delay
    (println "adding portal shutdown")
    (.addShutdownHook
    (Runtime/getRuntime)
    (Thread. #(do
                (println "closing portal...")
                (portal.api/close))))))

(defonce inspector (atom nil))

(defn open
  [& [opts]]
  (deref shutdown)
  (if-let [p (:portal (deref inspector))]
    (do (println "re-opening portal...")
        (portal/open p))
    (let [p (portal/open opts)]
      (println "opening portal...")
      (add-tap #'portal/submit)
      (reset! inspector {:portal p :opts opts}))))

(defn close
  []
  (when-let [p (:portal (deref inspector))]
    (remove-tap #'portal/submit)
    (portal/close p)
    (reset! inspector nil)))

(comment
  (open)
  (close)
  (deref inspector)
  (tap> {:foo [(range 5)]})
  )

;; Convenience namespace with short aliases for easy calling from REPL

#_{:clj-kondo/ignore [:namespace-name-mismatch]}
(ns p
  (:require [aperture.core :as ap]
            [portal.api :as portal]))

(defn o
  "Portal open."
  [& args]
  (apply ap/open args))

(defn c
  "Portal close."
  []
  (some-> ap/inspector deref :portal (portal/clear)))

(defn v
  "Portal selected value."
  []
  (some-> ap/inspector deref :portal deref))

(defn s
  "Portal status."
  []
  (some-> ap/inspector deref))

(defn x
  "Close portal."
  []
  (ap/close))

(comment
  (o)
  (x)
  (s)
  (v)
  (c)
  )
