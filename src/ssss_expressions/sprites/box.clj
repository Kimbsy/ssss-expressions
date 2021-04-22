(ns ssss-expressions.sprites.box
  (:require [quil.core :as q]
            [quip.sprite :as qpsprite]
            [quip.tween :as qptween]
            [quip.utils :as qpu]
            [ssss-expressions.common :as common]))

(defn lid-move
  [back?]
  (qptween/->tween
   :pos
   (if back? -200 200)
   :step-count 40
   :update-fn common/tween-x-fn))

(defn open-box
  [{:keys [current-scene] :as state}]
  (update-in state [:scenes current-scene :sprites]
             (fn [sprites]
               (map (fn [s]
                      (if (= :box-lid (:sprite-group s))
                        (-> s
                            (qptween/add-tween (lid-move false)))
                        s))
                    sprites))))

(defn close-box
  [{:keys [current-scene] :as state}]
  (update-in state [:scenes current-scene :sprites]
             (fn [sprites]
               (map (fn [s]
                      (if (= :box-lid (:sprite-group s))
                        (-> s
                            (qptween/add-tween (lid-move true)))
                        s))
                    sprites))))

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
