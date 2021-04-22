(ns ssss-expressions.scenes.level-01
  (:require [quil.core :as q]
            [quip.collision :as qpcollision]
            [quip.scene :as qpscene]
            [quip.sprite :as qpsprite]
            [quip.tween :as qptween]
            [quip.utils :as qpu]
            [ssss-expressions.common :as common]
            [ssss-expressions.delay :as delay]
            [ssss-expressions.sprites.hazard :as hazard]
            [ssss-expressions.sprites.snake :as snake]
            [ssss-expressions.sprites.rat :as rat]))

(defn draw-level-01
  [state]
  (qpu/background common/grey)
  (qpscene/draw-scene-sprites state))

(defn sprite-order-comparator
  [a b]
  (cond
    (= :player-snake (:sprite-group a))
    -1
    (= :player-snake (:sprite-group b))
    1
    :else
    0))

(defn update-scene-sprites-with-context
  "The `:player-snake` sprite needs to know which keys are currently held"
  [{:keys [current-scene] :as state}]
  (update-in state [:scenes current-scene :sprites]
             (fn [sprites]
               (map (fn [s]
                      (if (= :player-snake (:sprite-group s))
                        ((:update-fn s) s state)
                        ((:update-fn s) s)))
                    (sort-by :sprite-group sprite-order-comparator sprites)))))

(defn update-non-wrapped-non-squished-sprite-tweens
  "We don't want to apply tweens to rats that are wrapped"
  [{:keys [current-scene] :as state}]
  (let [sprites         (get-in state [:scenes current-scene :sprites])
        wrapped         (filter (fn [s] (#{:wrapped :squished} (:current-animation s)))
                                sprites)
        non-wrapped     (remove (fn [s] (#{:wrapped :squished} (:current-animation s)))
                                sprites)
        updated-sprites (transduce (comp (map qptween/update-sprite)
                                         (map qptween/handle-on-yoyos)
                                         (map qptween/handle-on-repeats)
                                         (map qptween/handle-on-completes))
                                   conj
                                   non-wrapped)
        cleaned-sprites (qptween/remove-completed-tweens updated-sprites)]
    (assoc-in state [:scenes current-scene :sprites]
              (concat cleaned-sprites wrapped))))

(defn update-level-01
  [state]
  (-> state
      common/handle-removal-flags
      update-scene-sprites-with-context
      snake/handle-direction-input
      update-non-wrapped-non-squished-sprite-tweens
      delay/update-delays
      qpcollision/update-collisions))

(defn print-state
  [{:keys [current-scene] :as state} e]
  (when (= 32 (:key-code e))
    (let [sprites (get-in state [:scenes current-scene :sprites])]
      (prn (:global-frame state))))
  state)

(defn colliders
  []
  [(qpcollision/collider
    :player-snake
    :rat
    qpcollision/identity-collide-fn
    (fn [{:keys [current-animation] :as r} _]
      (cond-> r
        (= :scurry current-animation)
        (->
         (qpsprite/set-animation :wrapped)
         (assoc :vel [0 0]))))
    :collision-detection-fn snake/body-intersects-w-h-rect
    :non-collide-fn-b (fn [{:keys [current-animation] :as r} _]
                        (cond-> r
                          (= :wrapped current-animation)
                          (qpsprite/set-animation :squished))))])

(defn tween-paths
  []
  [;; Round 1
   [{:starting-pos [(* 0.33 (q/width)) -100]
     :tweens [(qptween/->tween
               :vel
               4
               :step-count 1
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}
    {:starting-pos [(* 0.5 (q/width)) -100]
     :tweens [(qptween/->tween
               :vel
               4
               :step-count 10
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}
    {:starting-pos [(* 0.66 (q/width)) -100]
     :tweens [(qptween/->tween
               :vel
               4
               :step-count 20
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}]
   ;; Round 2
   [{:starting-pos [-100 (* 0.33 (q/height))]
     :tweens [(qptween/->tween
               :vel
               4
               :step-count 1
               :update-fn common/tween-x-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}
    {:starting-pos [-100 (* 0.5 (q/height))]
     :tweens [(qptween/->tween
               :vel
               4
               :step-count 10
               :update-fn common/tween-x-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}
    {:starting-pos [-100 (* 0.66 (q/height))]
     :tweens [(qptween/->tween
               :vel
               4
               :step-count 20
               :update-fn common/tween-x-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}]
   ;; Round 3
   [{:starting-pos [(* 1.1 (q/width)) (* 0.33 (q/height))]
     :tweens [(qptween/->tween
               :vel
               -4
               :step-count 1
               :update-fn common/tween-x-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}
    {:starting-pos [(* 1.1 (q/width)) (* 0.5 (q/height))]
     :tweens [(qptween/->tween
               :vel
               -4
               :step-count 10
               :update-fn common/tween-x-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}
    {:starting-pos [(* 1.1 (q/width)) (* 0.66 (q/height))]
     :tweens [(qptween/->tween
               :vel
               -4
               :step-count 20
               :update-fn common/tween-x-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}]])

(defn rats
  [paths]
  (for [{:keys [tweens starting-pos]} paths]
    (-> (rat/rat starting-pos)
        (assoc :tweens tweens))))

(defn hazards
  [paths]
  (for [{:keys [starting-pos]} paths]
    (hazard/hazard starting-pos)))

(defn sprites
  []
  [(snake/player-snake [120 225])])

(defn remove-tooltips
  [{:keys [current-scene] :as state}]
  (update-in state [:scenes current-scene :sprites]
             (fn [sprites]
               (map (fn [s]
                      (if (= :tooltip (:sprite-group s))
                        (common/flag-for-removal s)
                        s))
                    sprites))))

(defn hazard-tooltip
  [{:keys [current-scene] :as state}]
  (update-in state [:scenes current-scene :sprites]
             conj
             (qpsprite/text-sprite
              "Warnings show where mice will come from"
              [(* 0.5 (q/width))
               (* 0.3 (q/height))]
              :sprite-group :tooltip
              :color common/white)
             (qpsprite/text-sprite
              "Time to get in position!"
              [(* 0.5 (q/width))
               (* 0.4 (q/height))]
              :sprite-group :tooltip
              :color common/white)))

(defn get-score
  [{:keys [current-scene] :as state}]
  (->> (get-in state [:scenes current-scene :sprites])
       (map :current-animation)
       (filter #{:wrapped :squished})
       count))

(defn finish
  [{:keys [prev-level] :as state}]
  (-> state
      (assoc-in [:scores prev-level] (get-score state))
      (qpscene/transition :scoring
                          :transition-length 80
                          :init-fn (fn [state]
                                     (-> state
                                         (update :held-keys empty)
                                         (assoc :next-scene :level-02))))))

(defn delays
  []
  [(delay/add-sprites-to-scene-delay 100
                                     [(qpsprite/text-sprite
                                       "Use left and right arrows to move"
                                       [(* 0.5 (q/width))
                                        (* 0.3 (q/height))]
                                       :sprite-group :tooltip
                                       :color common/white)])
   (delay/->delay 500 remove-tooltips)
   (delay/add-sprites-to-scene-delay 600
                                     [(qpsprite/text-sprite
                                       "Warnings show where mice will come from"
                                       [(* 0.5 (q/width))
                                        (* 0.3 (q/height))]
                                       :sprite-group :tooltip
                                       :color common/white)])
   (delay/add-sprites-to-scene-delay 700 (hazards (nth (tween-paths) 0)))
   (delay/add-sprites-to-scene-delay 800
                                     [(qpsprite/text-sprite
                                       "Time to get in position!"
                                       [(* 0.5 (q/width))
                                        (* 0.4 (q/height))]
                                       :sprite-group :tooltip
                                       :color common/white)])
   (delay/add-sprites-to-scene-delay 1000 (rats (nth (tween-paths) 0)))
   (delay/->delay 1000 remove-tooltips)

   (delay/add-sprites-to-scene-delay (+ 1000 (* 60 6))
                                     (hazards (nth (tween-paths) 1)))
   (delay/add-sprites-to-scene-delay (+ 1000 (* 60 10))
                                     (rats (nth (tween-paths) 1)))

   (delay/add-sprites-to-scene-delay (+ 1000 (* 60 16))
                                     (hazards (nth (tween-paths) 2)))
   (delay/add-sprites-to-scene-delay (+ 1000 (* 60 20))
                                     (rats (nth (tween-paths) 2)))

   (delay/->delay (+ 1000 (* 60 26)) finish)])

(defn init
  []
  {:sprites (sprites)
   :delays (delays)
   :draw-fn draw-level-01
   :update-fn update-level-01
   :key-pressed-fns [print-state]
   :colliders (colliders)})
