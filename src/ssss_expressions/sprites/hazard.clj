(ns ssss-expressions.sprites.hazard
  (:require [quil.core :as q]
            [quip.sprite :as qpsprite]
            [quip.tween :as qptween]
            [quip.utils :as qpu]
            [ssss-expressions.common :as common]))

(def width 80)
(def height 80)
(def w2 (/ width 2))
(def h2 (/ height 2))

(defn bounded-pos
  [[x y]]
  [(max (+ 0 w2) (min x (- (q/width) w2)))
   (max (+ 0 h2) (min y (- (q/height) (* 0.6 h2))))])

(defn fade-out
  []
  (qptween/->tween
   :alpha
   -255
   :step-count 180
   :easing-fn qptween/exponential-easing-fn
   :on-complete-fn common/flag-for-removal))

(defn draw-hazard
  [{[x y] :pos :keys [pos alpha palette]}]
  (let [up [0 (- h2)]
        br (qpu/rotate-vector up 120)
        bl (qpu/rotate-vector br 120)
        [x1 y1] (map + pos up)
        [x2 y2] (map + pos br)
        [x3 y3] (map + pos bl)]
    (q/no-stroke)
    (qpu/fill (conj (case palette
                      :red common/pink
                      :blue common/dark-blue)
                    alpha))
    (q/triangle x1 y1 x2 y2 x3 y3)
    (qpu/fill (conj (case palette
                      :red common/highlight-pink
                      :blue common/light-blue)
                    alpha))
    (q/rect (- x 2) (- y 20) 4 24 2)
    (q/rect (- x 2) (+ y 10) 4 4 2)))

(defn hazard
  [incoming-pos & {:keys [palette] :or {palette :blue}}]
  {:sprite-group :hazard
   :pos (bounded-pos incoming-pos)
   :vel [0 0]
   :alpha 255
   :palette palette
   :rotation 0
   :points []
   :update-fn identity
   :draw-fn draw-hazard
   :tweens [(fade-out)]})
