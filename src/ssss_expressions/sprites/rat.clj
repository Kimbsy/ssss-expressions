(ns ssss-expressions.sprites.rat
  (:require [quil.core :as q]
            [quip.sprite :as qpsprite]
            [quip.tween :as qptween]
            [quip.utils :as qpu]
            [ssss-expressions.common :as common]))

(defn update-rotation
  [{[vx vy] :vel :as r}]
  (assoc r :rotation (- (qpu/rotation-angle [vx (- vy)]) 90)))

(defn update-rat
  [r]
  (-> r
      update-rotation
      qpsprite/update-animated-sprite))

(defn draw-rat
  [{[x y] :pos :keys [w h] :as r}]
  (qpsprite/draw-animated-sprite r)

  ;; draw rat collision boundary
  ;; (qpu/stroke qpu/red)
  ;; (q/no-fill)
  ;; (q/rect (- x 10) (- y 10) 15 15)
  )

(defn rat
  [pos]
  (->  (qpsprite/animated-sprite
        :rat
        pos
        48
        48
        "img/rat/rat.png"
        :animations {:none     {:frames      1
                                :y-offset    0
                                :frame-delay 100}
                     :scurry   {:frames      4
                                :y-offset    1
                                :frame-delay 4}
                     :wrapped  {:frames      2
                                :y-offset    2
                                :frame-delay 10}
                     :squished {:frames      1
                                :y-offset    3
                                :frame-delay 100}}
        :current-animation :scurry)
       (assoc :draw-fn draw-rat)
       (assoc :update-fn update-rat)))
