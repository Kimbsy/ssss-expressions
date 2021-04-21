(ns ssss-expressions.common
  (:require [quip.utils :as qpu]))

(def palette ["85ffc7" "297373" "ff8552" "e6e6e6"
              "39393a" "bd4089" "bd80a5" "33658a"
              "86bbd8" "9edafa"])

(def light-green [133 255 199])
(def dark-green [41 115 115])
(def orange [255 133 82])
(def white [230 230 230])
(def grey [57 57 58])
(def pink [189 64 137])
(def highlight-pink [189 128 165])
(def dark-blue [51 101 138])
(def light-blue [134 187 216])
(def highlight-blue [158 218 250])

(defn line-intersects-rect?
  [line
   {[x1 y1] :pos :keys [w h rotation]}]
  (let [x2 (+ x1 w)
        y2 (+ y1 h)]
    (some (partial qpu/lines-intersect? line)
          [[[x1 y1] [x2 y1]]
           [[x2 y1] [x2 y1]]
           [[x2 y2] [x1 y2]]
           [[x1 y2] [x1 y1]]])))

(defn tween-x-fn
  [[x y] d]
  [(+ x d) y])
(defn tween-y-fn
  [[x y] d]
  [x (+ y d)])
(defn tween-x-yoyo-fn
  [[x y] d]
  [(- x d) y])
(defn tween-y-yoyo-fn
  [[x y] d]
  [x (- y d)])

(defn flag-for-removal
  [s]
  (assoc s :remove? true))

(defn handle-removal-flags
  [{:keys [current-scene] :as state}]
  (update-in state [:scenes current-scene :sprites]
             (fn [sprites]
               (remove :remove? sprites))))
