(ns ssss-expressions.scenes.level-01
  (:require [quil.core :as q]
            [quip.scene :as qpscene]
            [quip.utils :as qpu]
            [ssss-expressions.common :as common]
            [ssss-expressions.sprites.snake :as snake]))

(defn draw-level-01
  [state]
  (qpu/background common/grey)
  (qpscene/draw-scene-sprites state)

  ;; (qpu/stroke common/light-green)
  ;; (q/stroke-weight 5)

  ;; (q/no-fill)

  ;; (q/begin-shape)
  ;; (q/curve-vertex 0 0)
  ;; (q/curve-vertex 0 0)
  ;; (q/curve-vertex 100 100)
  ;; (q/curve-vertex 200 100)
  ;; (q/curve-vertex 300 100)
  ;; (q/curve-vertex (q/mouse-x) (q/mouse-y))
  ;; (q/curve-vertex (q/mouse-x) (q/mouse-y))

  ;; (q/end-shape)


,  )

(defn update-level-01
  [state]
  (-> state
      (qpscene/update-scene-sprites)
      (snake/handle-direction-input)))

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
