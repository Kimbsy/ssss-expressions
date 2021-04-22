(ns ssss-expressions.scenes.level-03
  (:require [quil.core :as q]
            [quip.collision :as qpcollision]
            [quip.scene :as qpscene]
            [quip.sprite :as qpsprite]
            [quip.tween :as qptween]
            [quip.utils :as qpu]
            [ssss-expressions.common :as common]
            [ssss-expressions.delay :as delay]
            [ssss-expressions.scenes.scoring :as scoring]
            [ssss-expressions.sprites.hazard :as hazard]
            [ssss-expressions.sprites.snake :as snake]
            [ssss-expressions.sprites.rat :as rat]))

(defn draw-level-03
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

(defn update-level-03
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
      (prn (:body (first sprites)))))
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
   [{:starting-pos [(* 0.66 (q/width)) -100]
     :starting-vel [0 4]
     :tweens [(qptween/->tween
               :vel
               -4
               :step-count 200
               :update-fn common/tween-x-fn
               :easing-fn qptween/sigmoidal-easing-fn)
              (qptween/->tween
               :vel
               -4
               :step-count 200
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}
    {:starting-pos [(* 0.8 (q/width)) -100]
     :starting-vel [0 4]
     :tweens [(qptween/->tween
               :vel
               -4
               :step-count 220
               :update-fn common/tween-x-fn
               :easing-fn qptween/sigmoidal-easing-fn)
              (qptween/->tween
               :vel
               -4
               :step-count 220
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}
    {:starting-pos [(* 0.33 (q/width)) (* 1.1 (q/height))]
     :starting-vel [0 -4]
     :tweens [(qptween/->tween
               :vel
               4
               :step-count 200
               :update-fn common/tween-x-fn
               :easing-fn qptween/sigmoidal-easing-fn)
              (qptween/->tween
               :vel
               4
               :step-count 200
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}
    {:starting-pos [(* 0.2 (q/width)) (* 1.1 (q/height))]
     :starting-vel [0 -4]
     :tweens [(qptween/->tween
               :vel
               4
               :step-count 220
               :update-fn common/tween-x-fn
               :easing-fn qptween/sigmoidal-easing-fn)
              (qptween/->tween
               :vel
               4
               :step-count 220
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}]
   ;; Round 2
   [{:starting-pos [(* 0.8 (q/width)) -100]
     :starting-vel [0 4]
     :tweens [(qptween/->tween
               :vel
               -4
               :step-count 400
               :update-fn common/tween-x-fn
               :easing-fn qptween/sigmoidal-easing-fn)
              (qptween/->tween
               :vel
               -4
               :step-count 400
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)
              (qptween/->tween
               :vel
               4
               :step-count 700
               :update-fn common/tween-x-fn
               :easing-fn qptween/sigmoidal-easing-fn)
              (qptween/->tween
               :vel
               -4
               :step-count 700
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)
              (qptween/->tween
               :vel
               4
               :step-count 1000
               :update-fn common/tween-x-fn
               :easing-fn qptween/sigmoidal-easing-fn)
              (qptween/->tween
               :vel
               4
               :step-count 1000
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}
    {:starting-pos [(* 0.2 (q/width)) (+ 100 (q/height))]
     :starting-vel [0 -4]
     :tweens [(qptween/->tween
               :vel
               4
               :step-count 400
               :update-fn common/tween-x-fn
               :easing-fn qptween/sigmoidal-easing-fn)
              (qptween/->tween
               :vel
               4
               :step-count 400
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)
              (qptween/->tween
               :vel
               -4
               :step-count 700
               :update-fn common/tween-x-fn
               :easing-fn qptween/sigmoidal-easing-fn)
              (qptween/->tween
               :vel
               4
               :step-count 700
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)
              (qptween/->tween
               :vel
               -4
               :step-count 1000
               :update-fn common/tween-x-fn
               :easing-fn qptween/sigmoidal-easing-fn)
              (qptween/->tween
               :vel
               -4
               :step-count 1000
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}
    {:starting-pos [-100 (* 0.2 (q/height))]
     :starting-vel [4 0]
     :tweens [(qptween/->tween
               :vel
               -4
               :step-count 400
               :update-fn common/tween-x-fn
               :easing-fn qptween/sigmoidal-easing-fn)
              (qptween/->tween
               :vel
               4
               :step-count 400
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)
              (qptween/->tween
               :vel
               -4
               :step-count 700
               :update-fn common/tween-x-fn
               :easing-fn qptween/sigmoidal-easing-fn)
              (qptween/->tween
               :vel
               -4
               :step-count 700
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)
              (qptween/->tween
               :vel
               4
               :step-count 1000
               :update-fn common/tween-x-fn
               :easing-fn qptween/sigmoidal-easing-fn)
              (qptween/->tween
               :vel
               -4
               :step-count 1000
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}
    {:starting-pos [(+ 100 (q/width)) (* 0.8 (q/height))]
     :starting-vel [-4 0]
     :tweens [(qptween/->tween
               :vel
               4
               :step-count 400
               :update-fn common/tween-x-fn
               :easing-fn qptween/sigmoidal-easing-fn)
              (qptween/->tween
               :vel
               -4
               :step-count 400
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)
              (qptween/->tween
               :vel
               4
               :step-count 700
               :update-fn common/tween-x-fn
               :easing-fn qptween/sigmoidal-easing-fn)
              (qptween/->tween
               :vel
               4
               :step-count 700
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)
              (qptween/->tween
               :vel
               -4
               :step-count 1000
               :update-fn common/tween-x-fn
               :easing-fn qptween/sigmoidal-easing-fn)
              (qptween/->tween
               :vel
               4
               :step-count 1000
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}]
   ;; Round 3
   [{:starting-pos [(* 0.1 (q/width)) -100]
     :starting-vel [0 0]
     :tweens [(qptween/->tween
               :vel
               4
               :step-count 1
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}
    {:starting-pos [(* 0.2 (q/width)) -100]
     :starting-vel [0 0]
     :tweens [(qptween/->tween
               :vel
               4
               :step-count 60
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}
    {:starting-pos [(* 0.3 (q/width)) -100]
     :starting-vel [0 0]
     :tweens [(qptween/->tween
               :vel
               4
               :step-count 120
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}
    {:starting-pos [(* 0.4 (q/width)) -100]
     :starting-vel [0 0]
     :tweens [(qptween/->tween
               :vel
               4
               :step-count 180
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}
    {:starting-pos [(* 0.5 (q/width)) -100]
     :starting-vel [0 0]
     :tweens [(qptween/->tween
               :vel
               4
               :step-count 240
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}
    {:starting-pos [(* 0.6 (q/width)) -100]
     :starting-vel [0 0]
     :tweens [(qptween/->tween
               :vel
               4
               :step-count 300
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}
    {:starting-pos [(* 0.7 (q/width)) -100]
     :starting-vel [0 0]
     :tweens [(qptween/->tween
               :vel
               4
               :step-count 360
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}
    {:starting-pos [(* 0.8 (q/width)) -100]
     :starting-vel [0 0]
     :tweens [(qptween/->tween
               :vel
               4
               :step-count 420
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}
    {:starting-pos [(* 0.9 (q/width)) -100]
     :starting-vel [0 0]
     :tweens [(qptween/->tween
               :vel
               4
               :step-count 480
               :update-fn common/tween-y-fn
               :easing-fn qptween/sigmoidal-easing-fn)]}]
   ;; Round 4
   [{:starting-pos [(* 1.1 (q/width)) (- (* 0.3 (q/height)) 50)]
     :starting-vel [-2 0]
     :tweens [(qptween/->tween
               :pos
               100
               :step-count 40
               :yoyo? true
               :update-fn common/tween-y-fn
               :yoyo-update-fn common/tween-y-yoyo-fn
               :repeat-times ##Inf)]}
    {:starting-pos [-100 (- (* 0.6 (q/height)) 50)]
     :starting-vel [2 0]
     :tweens [(qptween/->tween
               :pos
               100
               :step-count 40
               :yoyo? true
               :update-fn common/tween-y-fn
               :yoyo-update-fn common/tween-y-yoyo-fn
               :repeat-times ##Inf)]}
    {:starting-pos [(- (* 0.5 (q/width)) 50) -100]
     :starting-vel [0 2]
     :tweens [(qptween/->tween
               :pos
               100
               :step-count 40
               :yoyo? true
               :update-fn common/tween-x-fn
               :yoyo-update-fn common/tween-x-yoyo-fn
               :repeat-times ##Inf)]}]])

(defn rats
  [paths]
  (for [{:keys [tweens starting-pos starting-vel]} paths]
    (-> (rat/rat starting-pos)
        (assoc :vel starting-vel)
        (assoc :tweens tweens))))

(defn hazards
  [paths]
  (for [{:keys [starting-pos]} paths]
    (hazard/hazard starting-pos :palette :red)))

(defn sprites
  []
  [(snake/player-snake [120 225])])

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
                                         (assoc-in [:scenes :scoring] (scoring/init))
                                         (update :held-keys empty)
                                         (assoc :next-scene :outro))))))

(defn delays
  []
  [(delay/add-sprites-to-scene-delay (* 60 2) (hazards (nth (tween-paths) 0)))
   (delay/add-sprites-to-scene-delay (* 60 6) (rats (nth (tween-paths) 0)))

   (delay/add-sprites-to-scene-delay (* 60 12) (hazards (nth (tween-paths) 1)))
   (delay/add-sprites-to-scene-delay (* 60 16) (rats (nth (tween-paths) 1)))

   (delay/add-sprites-to-scene-delay (* 60 22) (hazards (nth (tween-paths) 2)))
   (delay/add-sprites-to-scene-delay (* 60 26) (rats (nth (tween-paths) 2)))

   (delay/add-sprites-to-scene-delay (* 60 32) (hazards (nth (tween-paths) 3)))
   (delay/add-sprites-to-scene-delay (* 60 36) (rats (nth (tween-paths) 3)))
   (delay/add-sprites-to-scene-delay (+ 10 (* 60 36)) (rats (nth (tween-paths) 3)))
   (delay/add-sprites-to-scene-delay (+ 20 (* 60 36)) (rats (nth (tween-paths) 3)))
   (delay/add-sprites-to-scene-delay (+ 30 (* 60 36)) (rats (nth (tween-paths) 3)))
   (delay/add-sprites-to-scene-delay (+ 40 (* 60 36)) (rats (nth (tween-paths) 3)))
   (delay/add-sprites-to-scene-delay (+ 50 (* 60 36)) (rats (nth (tween-paths) 3)))
   (delay/add-sprites-to-scene-delay (+ 60 (* 60 36)) (rats (nth (tween-paths) 3)))
   (delay/add-sprites-to-scene-delay (+ 70 (* 60 36)) (rats (nth (tween-paths) 3)))
   (delay/add-sprites-to-scene-delay (+ 80 (* 60 36)) (rats (nth (tween-paths) 3)))
   (delay/add-sprites-to-scene-delay (+ 90 (* 60 36)) (rats (nth (tween-paths) 3)))
   (delay/add-sprites-to-scene-delay (+ 100 (* 60 36)) (rats (nth (tween-paths) 3)))

   (delay/->delay (* 60 50) finish)])

(defn init
  []
  {:sprites (sprites)
   :delays (delays)
   :draw-fn draw-level-03
   :update-fn update-level-03
   :key-pressed-fns [print-state]
   :colliders (colliders)})
