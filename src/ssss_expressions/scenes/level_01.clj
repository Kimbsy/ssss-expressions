(ns ssss-expressions.scenes.level-01
  (:require [quil.core :as q]
            [quip.collision :as qpcollision]
            [quip.scene :as qpscene]
            [quip.sprite :as qpsprite]
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
  [{:keys [current-scene] :as state}]
  (update-in state [:scenes current-scene :sprites]
             (fn [sprites]
               (map (fn [s]
                      (if (= :player-snake (:sprite-group s))
                        ((:update-fn s) s state)
                        ((:update-fn s) s)))
                    (sort-by :sprite-group sprite-order-comparator sprites)))))

(defn update-level-01
  [state]
  (-> state
      update-scene-sprites-with-context
      snake/handle-direction-input
      qpcollision/update-collisions))

(defn sprites
  []
  [(snake/player-snake [200 200])
   (rat/rat [(+ (rand-int 66) 300) (+ (rand-int 100) 200)])
   (rat/rat [(+ (rand-int 66) 300) (+ (rand-int 100) 500)])
   (rat/rat [(+ (rand-int 66) 633) (+ (rand-int 100) 200)])
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
      (if (= :scurry current-animation)
        (qpsprite/set-animation r :wrapped)
        r))
    :collision-detection-fn snake/body-intersects-w-h-rect
    :non-collide-fn-b (fn [{:keys [current-animation] :as r} _]
                        (if (= :wrapped current-animation)
                          (qpsprite/set-animation r :scurry)
                          r)))])

(defn init
  []
  {:sprites (sprites)
   :draw-fn draw-level-01
   :update-fn update-level-01
   :key-pressed-fns [print-state]
   :colliders (colliders)})
