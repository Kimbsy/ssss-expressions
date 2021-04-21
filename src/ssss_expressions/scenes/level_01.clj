(ns ssss-expressions.scenes.level-01
  (:require [quil.core :as q]
            [quip.collision :as qpcollision]
            [quip.scene :as qpscene]
            [quip.sprite :as qpsprite]
            [quip.tween :as qptween]
            [quip.utils :as qpu]
            [ssss-expressions.common :as common]
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
      update-scene-sprites-with-context
      snake/handle-direction-input
      update-non-wrapped-non-squished-sprite-tweens
      qpcollision/update-collisions))

(defn sprites
  []
  [(snake/player-snake [200 200])
   (rat/rat [(+ (rand-int 66) 300) (+ (rand-int 100) 200)])
   (rat/rat [(+ (rand-int 66) 300) (+ (rand-int 100) 200)])
   (rat/rat [(+ (rand-int 66) 300) (+ (rand-int 100) 500)])
   (rat/rat [(+ (rand-int 66) 300) (+ (rand-int 100) 500)])
   (rat/rat [(+ (rand-int 66) 633) (+ (rand-int 100) 200)])
   (rat/rat [(+ (rand-int 66) 633) (+ (rand-int 100) 200)])
   (rat/rat [(+ (rand-int 66) 633) (+ (rand-int 100) 500)])
   (rat/rat [(+ (rand-int 66) 633) (+ (rand-int 100) 500)])])

(defn print-state
  [{:keys [current-scene] :as state} e]
  (when (= 32 (:key-code e))
    (prn (:held-keys state)))
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
                          (qpsprite/set-animation :squished)

                          (and (zero? (reduce + (:vel r)))
                               (not (#{:wrapped :squished} current-animation)))
                          (assoc :vel [(- (rand-int 4) 2) (- (rand-int 4) 2)]))))])

(defn delays
  []
  [])

(defn paths
  []
  [])

(defn init
  []
  {:sprites (sprites)
   :draw-fn draw-level-01
   :update-fn update-level-01
   :key-pressed-fns [print-state]
   :colliders (colliders)
   :delays (delays)})
