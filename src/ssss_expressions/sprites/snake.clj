(ns ssss-expressions.sprites.snake
  (:require [quil.core :as q]
            [quip.sprite :as qpsprite]
            [quip.utils :as qpu]
            [ssss-expressions.common :as common]))

(defn update-player-snake
  [s]
  s)

(defn draw-player-snake
  [{[x y] :pos :as s}]
  (qpu/fill common/orange)
  (q/rect x y 10 10)
  )

(defn player-snake
  [pos]
  {:sprite-group :player-snake
   :uuid (java.util.UUID/randomUUID)
   :pos pos
   :points []
   :update-fn update-player-snake
   :draw-fn draw-player-snake
   :bounds-fn (constantly false) ; ... this is gonna take some thinking
   })

(defn theatrical-snake
  [color pos]
  (qpsprite/animated-sprite
   :theatrical-snake
   pos
   192
   384
   (str "img/snake/" (name color) ".png")
   :animations {:none   {:frames      1
                         :y-offset    0
                         :frame-delay 100}
                :tongue {:frames 5
                         :y-offset 1
                         :frame-delay 4}}
   :current-animation :tongue
   ))
