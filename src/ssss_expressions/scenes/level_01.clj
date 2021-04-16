(ns ssss-expressions.scenes.level-01
  (:require [quil.core :as q]
            [quip.scene :as qpscene]
            [ssss-expressions.sprites.snake :as snake]))

(defn draw-level-01
  [state]
  (qpscene/draw-scene-sprites state))

(defn update-level-01
  [state]
  (-> state
      (qpscene/update-scene-sprites)))

(defn sprites
  []
  [(snake/player-snake [100 100])])

(defn init
  []
  {:sprites (sprites)
   :draw-fn draw-level-01
   :update-fn update-level-01})
