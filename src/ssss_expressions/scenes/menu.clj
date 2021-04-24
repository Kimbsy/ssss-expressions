(ns ssss-expressions.scenes.menu
  (:require [ssss-expressions.common :as common]
            [ssss-expressions.delay :as delay]
            [ssss-expressions.sprites.snake :as snake]
            [quil.core :as q]
            [quip.sprites.button :as qpbutton]
            [quip.scene :as qpscene]
            [quip.sound :as qpsound]
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
  (qpscene/transition state :intro
                      :transition-length 30
                      :init-fn (fn [state]
                                 (qpsound/stop-music)
                                 (qpsound/loop-music "music/Chansssse_Encounter.wav")
                                 (common/unclick-all-buttons state))))

(defn on-click-credits
  [state e]
  (qpscene/transition state :credits
                      :transition-length 30
                      :init-fn (fn [state]
                                 (common/unclick-all-buttons state))))

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
   -400
   :step-count 79
   :update-fn common/tween-y-fn))

(defn popdown-tween
  []
  (qptween/->tween
   :pos
   400
   :step-count 79
   :update-fn common/tween-y-fn))

(defn theatrical-snakes
  []
  [(snake/theatrical-snake :green
                           [(* 0.2 (q/width))
                            (* 1.5 (q/height))]
                           :current-animation :none)
   (snake/theatrical-snake :orange
                           [(* 0.8 (q/width))
                            (* -0.5 (q/height))]
                           :rotation 180
                           :current-animation :none)])

(defn draw-menu
  [state]
  (qpu/background common/dark-green)
  (qpscene/draw-scene-sprites state))

(defn update-menu
  [state]
  (-> state
      qpscene/update-scene-sprites
      qptween/update-sprite-tweens
      delay/update-delays))

(defn sprites
  []
  (concat (title-sprites)
          (buttons)
          (theatrical-snakes)))

(defn hiss
  [snake-key]
  (fn [{:keys [current-scene] :as state}]
    (qpsound/play "hiss/hiss1.wav")
    (-> state
        (update-in [:scenes current-scene :sprites]
                   (fn [sprites]
                     (map (fn [s]
                            (if (= snake-key (:sprite-group s))
                              (qpsprite/set-animation s :tongue)
                              s))
                          sprites))))))

(defn green-down
  [{:keys [current-scene] :as state}]
  (-> state
      (update-in [:scenes current-scene :sprites]
                 (fn [sprites]
                   (map (fn [s]
                          (if (= :green-snake (:sprite-group s))
                            (-> s
                                (qpsprite/set-animation :none)
                                (qptween/add-tween (popdown-tween)))
                            s))
                        sprites)))))

(defn green-up
  [{:keys [current-scene] :as state}]
  (-> state
      (update-in [:scenes current-scene :sprites]
                 (fn [sprites]
                   (map (fn [s]
                          (if (= :green-snake (:sprite-group s))
                            (qptween/add-tween s (popup-tween))
                            s))
                        sprites)))
      (delay/add-delay 95 (hiss :green-snake))
      (delay/add-delay 119 green-down)
      (delay/add-delay 577 green-up)))

(defn orange-up
  [{:keys [current-scene] :as state}]
  (-> state
      (update-in [:scenes current-scene :sprites]
                 (fn [sprites]
                   (map (fn [s]
                          (if (= :orange-snake (:sprite-group s))
                            (-> s
                                (qpsprite/set-animation :none)
                                (qptween/add-tween (popup-tween)))
                            s))
                        sprites)))))

(defn orange-down
  [{:keys [current-scene] :as state}]
  (-> state
      (update-in [:scenes current-scene :sprites]
                 (fn [sprites]
                   (map (fn [s]
                          (if (= :orange-snake (:sprite-group s))
                            (qptween/add-tween s (popdown-tween))
                            s))
                        sprites)))
      (delay/add-delay 95 (hiss :orange-snake))
      (delay/add-delay 119 orange-up)
      (delay/add-delay 577 orange-down)))

(defn delays
  []
  (let [initial-delay 1
        delays [[0 green-up]
                [136 orange-down]
                [286 green-up]
                [0 orange-down]]]
    (:ds (reduce (fn [{:keys [ds curr] :as acc}
                      [d f]]
                   (-> acc
                       (update :ds conj (delay/->delay (+ curr d) f))
                       (update :curr + d)))
                 {:ds []
                  :curr initial-delay}
                 delays))))

(defn init
  []
  {:sprites (sprites)
   :delays (delays)
   :draw-fn draw-menu
   :update-fn update-menu
   :mouse-pressed-fns [qpbutton/handle-buttons-pressed]
   :mouse-released-fns [qpbutton/handle-buttons-released]})
