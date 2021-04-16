(ns ssss-expressions.core
  (:gen-class)
  (:require [quil.core :as q]
            [quip.core :as qp]
            [quip.scene :as qpscene]
            [quip.sprite :as qpsprite]
            [quip.sound :as qpsound]
            [quip.utils :as qpu]
            [ssss-expressions.scenes.credits :as credits]
            [ssss-expressions.scenes.intro :as intro]
            [ssss-expressions.scenes.level-01 :as level-01]
            [ssss-expressions.scenes.menu :as menu]
            [ssss-expressions.scenes.outro :as outro]))

(defn setup
  []
  (qpsound/loop-music "music/Romantic_and_Triumphant_Victory.wav")
  {:default-font (q/create-font "font/UbuntuMono-Regular.ttf" qpu/default-text-size)
   :giant-font   (q/create-font "font/UbuntuMono-Regular.ttf" 250)})

(defn init-scenes
  []
  {:menu     (menu/init)
   :intro    (intro/init)
   :level-01 (level-01/init)
   :outro    (outro/init)
   :credits  (credits/init)})

(defn cleanup
  [state]
  (qpsound/stop-music)
  #_(System/exit 0))

(def ssss-expressions-game
  (qp/game
   {:title          "Ssss Expressions"
    :size           [1000 800]
    :setup          setup
    :init-scenes-fn init-scenes
    :current-scene  :level-01}))

(defn -main
  [& args]
  (qp/run ssss-expressions-game))
