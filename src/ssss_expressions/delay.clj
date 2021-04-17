(ns ssss-expressions.delay)

;;;; Delays should be added to scenes and updated during the scene
;;;; update.  During the delay update we should dec all the timers and
;;;; then reduce applying the F's of any at 0 across the state.

;;;; @TODO: They should be able to be flagged for removal during
;;;; transitions, or be left alone (default tbd).

(defn ->delay
  [remaining f]
  {:remaining remaining
   :on-complete-fn f})

(defn add-delay
  [{:keys [current-scene] :as state} remaining f]
  (let [delay (->delay remaining f)
        path [:scenes current-scene :delays]]
    (if (seq (get-in state path))
      (update-in state path conj delay)
      (assoc-in state path [delay]))))

(defn apply-all
  [state fs]
  (reduce (fn [state f] (f state))
          state
          fs))

(defn update-delay
  [d]
  (update d :remaining dec))

(defn update-delays
  [{:keys [current-scene] :as state}]
  (let [path [:scenes current-scene :delays]
        delays (get-in state path)]
    (if (seq delays)
      (let [updated-delays (map update-delay delays)
            finished (filter #(zero? (:remaining %)) updated-delays)
            unfinished (remove #(zero? (:remaining %)) updated-delays)]
        (-> state
            (assoc-in path unfinished)
            (apply-all (map :on-complete-fn finished))))
      state)))
