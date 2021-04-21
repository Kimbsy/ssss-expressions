(ns ssss-expressions.sprites.box
  (:require [quil.core :as q]
            [quip.sprite :as qpsprite]
            [quip.utils :as qpu]))

(defn box-body
  [pos]
  (qpsprite/image-sprite
   :box-body
   pos
   208
   164
   "img/cutscene/big-box-body.png"))

(defn box-lid
  [pos]
  (qpsprite/image-sprite
   :box-lid
   pos
   208
   104
   "img/cutscene/big-box-lid.png"
   :rotation 0))
