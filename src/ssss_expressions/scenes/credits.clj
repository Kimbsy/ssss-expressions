(ns ssss-expressions.scenes.credits
  (:require [quil.core :as q]
            [quip.scene :as qpscene]
            [quip.sound :as qpsound]
            [quip.sprite :as qpsprite]
            [quip.sprites.button :as qpbutton]
            [quip.utils :as qpu]
            [ssss-expressions.common :as common]))

(defn draw-credits
  [state]
  (qpu/background common/dark-green)

  (qpu/stroke common/grey)
  (q/stroke-weight 5)
  (qpu/fill common/white)

  (let [w (* 0.7 (q/width))
        h (* 0.7 (q/height))
        x (- (* 0.5 (q/width)) (* 0.5 w))
        y (- (* 0.5 (q/height)) (* 0.5 h))]
    (q/rect x y w h))
  (qpscene/draw-scene-sprites state))

(defn on-click-menu
  [state e]
  (qpscene/transition state :menu
                      :transition-length 30
                      :init-fn (fn [state]
                                 (qpsound/stop-music)
                                 (qpsound/loop-music "music/Menu_Music_w_breaks.wav")
                                 (common/unclick-all-buttons state))))

(defn sprites
  []
  [(qpsprite/text-sprite "Thanks for playing!" [(* 0.5 (q/width))
                                                (* 0.3 (q/height))]
                         :font qpu/bold-font
                         :size qpu/large-text-size)
   (qpsprite/text-sprite "Gameplay and art:" [(* 0.5 (q/width))
                                              (* 0.4 (q/height))])
   (qpsprite/text-sprite "Dave Kimber" [(* 0.5 (q/width))
                                        (* 0.45 (q/height))]
                         :color common/dark-blue
                         :font qpu/bold-font)

   (qpsprite/text-sprite "Music:" [(* 0.5 (q/width))
                                   (* 0.55 (q/height))])
   (qpsprite/text-sprite "PJ Kimber" [(* 0.5 (q/width))
                                      (* 0.6 (q/height))]
                         :color common/dark-blue
                         :font qpu/bold-font)
   (qpbutton/button-sprite "Menu"
                           [(* 0.5 (q/width))
                            (* 0.72 (q/height))]
                           :color common/dark-green
                           :content-color common/white
                           :on-click on-click-menu)])

(defn init
  []
  {:draw-fn draw-credits
   :sprites (sprites)
   :mouse-pressed-fns [qpbutton/handle-buttons-pressed]
   :mouse-released-fns [qpbutton/handle-buttons-released]})
