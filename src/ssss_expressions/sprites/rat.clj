(ns ssss-expressions.sprites.rat
  (:require [quil.core :as q]
            [quip.sprite :as qpsprite]
            [quip.utils :as qpu]
            [ssss-expressions.common :as common]))

;; @TODO: make rats move

;; (defn wrap-pos
;;   [{[x y] :pos :as r}]
;;   )

;; (defn update-rat
;;   [r]
;;   (-> r
;;       qpsprite/update-animated-sprite
;;       wrap-pos))

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
        :animations {:none   {:frames      1
                              :y-offset    0
                              :frame-delay 100}
                     :scurry {:frames 4
                              :y-offset 1
                              :frame-delay 2}
                     :wrapped {:frames 2
                               :y-offset 2
                               :frame-delay 10}}
        :current-animation :scurry)
       (assoc :draw-fn draw-rat)))
