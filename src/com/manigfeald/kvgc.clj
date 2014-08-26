(ns com.manigfeald.kvgc)

;; must be reducible
(defprotocol Heap
  (references [heap ptr]
    "returns a list/sequence of ptrs that the data behind a given
    pointer points to")
  (tag [heap ptr tag-value]
    "tag the given pointer with the given value")
  (tag-value [heap ptr]
    "return the tag value for a given pointer")
  (free [heap ptr]
    "free/delete/dissoc the data for the given pointer")
  (with-heap-lock [heap fun]
    "apply fun to heap while blocking others from operating on the
    heap (locking is a nop for value based heaps)"))

(defn mark [heap root-set mark-value]
  (reduce
   (fn [heap ptr]
     (tag heap ptr mark-value))
   heap
   (for [ptr root-set
         tree-item (tree-seq (comp seq (partial references heap))
                             (partial references heap)
                             ptr)
         :when (not (seq? tree-item))
         :let [ptr tree-item]
         :when ptr]
     ptr)))

(defn sweep [heap mark-value]
  (reduce
   (fn [heap [ptr _]]
     (if (= (tag-value heap ptr) mark-value)
       heap
       (free heap ptr)))
   heap
   heap))

(defn gc [heap root-set pre-mark-state]
  (let [new-mark-state (not pre-mark-state)]
    (with-heap-lock heap
      (fn [heap]
        (let [heap (mark heap root-set new-mark-state)
              heap (sweep heap new-mark-state)]
          [heap new-mark-state])))))

