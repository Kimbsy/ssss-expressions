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
            [ssss-expressions.scenes.level-02 :as level-02]
            [ssss-expressions.scenes.level-03 :as level-03]
            [ssss-expressions.scenes.menu :as menu]
            [ssss-expressions.scenes.outro :as outro]
            [ssss-expressions.scenes.scoring :as scoring]))

(defn setup
  []
  (qpsound/loop-music "music/Menu_Music_w_breaks.wav")
  {:default-font (q/create-font "font/UbuntuMono-Regular.ttf" qpu/default-text-size)
   :giant-font   (q/create-font "font/UbuntuMono-Regular.ttf" 250)
   :scores {}
   :prev-level :none})

(defn init-scenes
  []
  {:menu     (menu/init)
   :intro    (intro/init)
   :level-01 (level-01/init)
   :level-02 (level-02/init)
   :level-03 (level-03/init)
   :outro    (outro/init)
   :credits  (credits/init)
   :scoring  (scoring/init)})

(defn cleanup
  [state]
  (qpsound/stop-music)
  (System/exit 0))

(def ssss-expressions-game
  (qp/game
   {:title          "Ssss Expressions"
    :size           [1000 800]
    :setup          setup
    :init-scenes-fn init-scenes
    :current-scene  :menu}))

(defn -main
  [& args]
  (qp/run ssss-expressions-game))
