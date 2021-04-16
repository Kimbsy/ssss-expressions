(ns ssss-expressions.scenes.menu
  (:require [ssss-expressions.common :as common]
            [quil.core :as q]
            [quip.scene :as qpscene]
            [quip.utils :as qpu]))

(defn draw-menu
  [state]
  (qpu/background common/dark-green))

(defn init
  []
  {:draw-fn draw-menu})
