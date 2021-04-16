(ns ssss-expressions.scenes.menu
  (:require [ssss-expressions.common :as common]
            [ssss-expressions.sprites.snake :as snake]
            [quil.core :as q]
            [quip.sprites.button :as qpbutton]
            [quip.scene :as qpscene]
            [quip.sprite :as qpsprite]
            [quip.tween :as qptween]
            [quip.utils :as qpu]))

(defn title-sprites
  []
  [(qpsprite/text-sprite "(Ssss-"
                         [(* 0.07 (q/width))
                          70]
                         :size qpu/title-text-size
                         :color common/white
                         :offsets [:left :top])
   (qpsprite/text-sprite "  Expressions)"
                         [(* 0.07 (q/width))
                          190]
                         :size qpu/title-text-size
                         :color common/white
                         :offsets [:left :top])])

(defn on-click-play
  [state e]
  (qpscene/transition state :level-01
                      :transition-length 30
                      :init-fn (fn [state]
                                 state)))

(defn on-click-credits
  [state e]
  (qpscene/transition state :credits
                      :transition-length 30
                      :init-fn (fn [state]
                                 state)))

(defn on-click-quit
  [state e]
  (q/exit))

(defn buttons
  []
  [(qpbutton/button-sprite "Play"
                           [(* 0.2 (q/width))
                            (* 0.57 (q/height))]
                           :color common/grey
                           :content-color common/white
                           :on-click on-click-play)
   (qpbutton/button-sprite "Credits"
                           [(* 0.5 (q/width))
                            (* 0.57 (q/height))]
                           :color common/grey
                           :content-color common/white
                           :on-click on-click-credits)
   (qpbutton/button-sprite "Quit"
                           [(* 0.8 (q/width))
                            (* 0.57 (q/height))]
                           :color common/grey
                           :content-color common/white
                           :on-click on-click-quit)])

(defn popup-tween
  []
  (qptween/->tween
   :pos
   400
   :easing-fn qptween/asymptotic-easing-fn
   :update-fn (fn [[x y] d]
                [x (- y d)])
   :yoyo? true
   :yoyo-update-fn (fn [[x y] d]
                     [x (+ y d)])
   :on-complete-fn (fn [s]
                     (qptween/add-tween s (popup-tween)))))

(defn popdown-tween
  []
  (qptween/->tween
   :pos
   600
   :step-count 140
   :easing-fn qptween/asymptotic-easing-fn
   :update-fn (fn [[x y] d]
                [x (+ y d)])
   :yoyo? true
   :yoyo-update-fn (fn [[x y] d]
                     [x (- y d)])
   :on-complete-fn (fn [s]
                     (qptween/add-tween s (popdown-tween)))))

(defn theatrical-snakes
  []
  [(qptween/add-tween
    (snake/theatrical-snake :green
                            [(* 0.2 (q/width))
                             (* 1.5 (q/height))])
    (popup-tween))
   (qptween/add-tween
    (snake/theatrical-snake :orange
                            [(* 0.8 (q/width))
                             (* -0.8 (q/height))]
                            :rotation 180)
    (-> (popdown-tween)
        ;; (assoc :yoyoing? true)
        ;; (assoc :progress 150)
        ))])

(defn draw-menu
  [state]
  (qpu/background common/dark-green)
  (qpscene/draw-scene-sprites state))

(defn update-menu
  [state]
  (-> state
      qpscene/update-scene-sprites
      qptween/update-sprite-tweens))

(defn sprites
  []
  (concat (title-sprites)
          (buttons)
          (theatrical-snakes)))

(defn init
  []
  {:sprites (sprites)
   :draw-fn draw-menu
   :update-fn update-menu
   :mouse-pressed-fns [qpbutton/handle-buttons-pressed]
   :mouse-released-fns [qpbutton/handle-buttons-released]})
