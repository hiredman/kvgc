(ns com.manigfeald.kvgc-test
  (:require [clojure.test :refer :all]
            [com.manigfeald.kvgc :refer :all]
            ;; [amontillado.cask :as cask]
            ))

(extend-protocol Heap
  clojure.lang.IPersistentMap
  (references [heap ptr]
    (:references (get heap ptr)))
  (tag [heap ptr tag-value]
    (assoc-in heap [ptr :tag] tag-value))
  (tag-value [heap ptr]
    (get-in heap [ptr :tag]))
  (free [heap ptr]
    (dissoc heap ptr))
  (with-heap-lock [heap fun]
    (fun heap)))

(deftest t-map-gc
  (let [h (assoc (hash-map)
            :one {:references ()
                  :value [:value 1]}
            :two {:references ()
                  :value [:value 2]}
            :cons {:references (list :one :two)
                   :value [:cons :one :two]}
            :three {:references ()
                    :value [:value 3]})
        [h ms] (gc h #{:cons :three} false)
        _ (is (contains? h :cons))
        _ (is (contains? h :one))
        _ (is (contains? h :two))
        _ (is (contains? h :three))
        [h ms] (gc h #{:three} ms)
        _ (is (= [:three] (keys h)) h)]
    ))

;; (deftest t-cask-gc
;;   (let [dir (doto (java.io.File. "/tmp/bar")
;;               (.mkdirs))
;;         cask (cask/open-bitcask dir)
;;         tags (java.util.HashMap.)
;;         h (reify
;;             clojure.core.protocols/CollReduce
;;             (coll-reduce [coll f]
;;               (reduce f
;;                       (for [k (cask/cask-keys cask)]
;;                         [(String. k) nil])))
;;             (coll-reduce [coll f start]
;;               (reduce f
;;                       start
;;                       (for [k (cask/cask-keys cask)]
;;                         [(String. k) nil])))
;;             Heap
;;             (references [heap ptr]
;;               (when-let [v (cask/read-key cask (.getBytes ptr))]
;;                 (:references (read-string (String. v)))))
;;             (tag [heap ptr tag-value]
;;               (.put tags ptr tag-value)
;;               heap)
;;             (tag-value [heap ptr]
;;               (.get tags ptr))
;;             (free [heap ptr]
;;               (cask/delete-key cask (.getBytes ptr))
;;               (.remove tags ptr)
;;               heap)
;;             (with-heap-lock [heap fun]
;;               (locking heap
;;                 (fun heap))))]
;;     (doto cask
;;       (cask/write-key (.getBytes "one")
;;                       (.getBytes (pr-str {:references ()
;;                                           :value [:value 1]})))
;;       (cask/write-key (.getBytes "two")
;;                       (.getBytes (pr-str {:references ()
;;                                           :value [:value 2]})))
;;       (cask/write-key (.getBytes "three")
;;                       (.getBytes (pr-str {:references ()
;;                                           :value [:value 3]})))
;;       (cask/write-key (.getBytes "cons")
;;                       (.getBytes (pr-str {:references (list "one" "two")
;;                                           :value [:cons "one" "two"]}))))
;;     (let [[h ms] (gc h #{"cons" "three"} false)
;;           _ (is (cask/read-key cask (.getBytes "cons")))
;;           _ (is (cask/read-key cask (.getBytes "one")))
;;           _ (is (cask/read-key cask (.getBytes "two")))
;;           _ (is (cask/read-key cask (.getBytes "three")))
;;           [h ms] (gc h #{"three"} ms)
;;           _ (is (cask/read-key cask (.getBytes "three")))
;;           _ (is (not (cask/read-key cask (.getBytes "cons"))))
;;           _ (is (not (cask/read-key cask (.getBytes "one"))))
;;           _ (is (not (cask/read-key cask (.getBytes "two"))))
;;           ]
;;       )
;;     (doseq [f (reverse (file-seq dir))]
;;       (.delete f))))
