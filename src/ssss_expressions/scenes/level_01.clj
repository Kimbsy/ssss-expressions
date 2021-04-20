(ns ssss-expressions.scenes.level-01
  (:require [quil.core :as q]
            [quip.scene :as qpscene]
            [quip.utils :as qpu]
            [ssss-expressions.common :as common]
            [ssss-expressions.sprites.snake :as snake]))

(defn draw-level-01
  [state]
  (qpu/background common/grey)
  (qpscene/draw-scene-sprites state))

(defn update-scene-sprites-with-context
  [{:keys [current-scene] :as state}]
  (update-in state [:scenes current-scene :sprites]
             (fn [sprites]
               (map (fn [s]
                      ((:update-fn s) s state))
                    sprites))))

(defn update-level-01
  [state]
  (-> state
      update-scene-sprites-with-context
      snake/handle-direction-input))

(defn sprites
  []
  [(snake/player-snake [200 200])])

(defn print-state
  [{:keys [current-scene] :as state} e]
  (when (= 32 (:key-code e))
    (prn (:held-keys state)))
  state)

(defn init
  []
  {:sprites (sprites)
   :draw-fn draw-level-01
   :update-fn update-level-01
   :key-pressed-fns [print-state]})
