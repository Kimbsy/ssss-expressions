(ns ssss-expressions.scenes.intro
  (:require [ssss-expressions.common :as common]
            [ssss-expressions.delay :as delay]
            [ssss-expressions.sprites.snake :as snake]
            [quil.core :as q]
            [quip.scene :as qpscene]
            [quip.sprite :as qpsprite]
            [quip.sound :as qpsound]
            [quip.tween :as qptween]
            [quip.utils :as qpu]))

(defn update-intro
  [state]
  (-> state
      qpscene/update-scene-sprites
      qptween/update-sprite-tweens
      delay/update-delays))

(defn draw-intro
  [state]
  (qpu/background common/dark-green)
  (qpscene/draw-scene-sprites state))

(defn green-pops-up
  [state]
  (let [pos [(* 0.2 (q/width)) (* 1.4 (q/height))]
        tween (qptween/->tween
               :pos
               400
               :easing-fn qptween/asymptotic-easing-fn
               :update-fn (fn [[x y] d]
                            [x (- y d)]))
        snake (qptween/add-tween
               (snake/theatrical-snake :green pos)
               tween)]
    (update-in state [:scenes :intro :sprites] conj snake)))

(defn green-hiss
  [state]
  (qpsound/play "hiss/hiss1.wav")
  (update-in state [:scenes :intro :sprites]
             (fn [sprites]
               (map (fn [s]
                      (if (= :green-snake (:sprite-group s))
                        (qpsprite/set-animation s :tongue)
                        s))
                    sprites))))

(defn green-stop-hiss
  [state]
  (update-in state [:scenes :intro :sprites]
             (fn [sprites]
               (map (fn [s]
                      (if (= :green-snake (:sprite-group s))
                        (qpsprite/set-animation s :none)
                        s))
                    sprites))))

(defn green-walk-right
  [state]
  (let [tween (qptween/->tween
               :pos 80
               :step-count 40
               :update-fn (fn [[x y] d] [(+ x d) (+ y (/ d 3))])
               :yoyo? true
               :yoyo-update-fn (fn [[x y] d] [(- x d) (- y (/ d 3))])
               :repeat-times 3)]
    (update-in state [:scenes :intro :sprites]
               (fn [[green-snake]]
                 [(qptween/add-tween
                   green-snake
                   tween)]))))

(defn orange-pulled-left
  [state]
  (let [pos [(* 1.2 (q/width)) (- (* 1.4 (q/height)) 400)]
        tween (qptween/->tween
               :pos
               250
               :step-count 80
               :update-fn (fn [[x y] d] [(- x d) y]))
        snake (qptween/add-tween
               (snake/theatrical-snake :orange pos)
               tween)]
    (update-in state [:scenes :intro :sprites] conj snake)))

(defn orange-turn
  [state]
  (update-in state [:scenes :intro :sprites]
             (fn [sprites]
               (map (fn [s]
                      (if (= :orange-snake (:sprite-group s))
                        (-> s
                            (assoc :spritesheet (q/load-image "img/snake/orange-flipped.png"))
                            (update :pos (fn [[x y]] [(- x 150) y])))
                        s))
                    sprites))))

(defn orange-hiss
  [state]
  (qpsound/play "hiss/hiss1.wav")
  (update-in state [:scenes :intro :sprites]
             (fn [sprites]
               (map (fn [s]
                      (if (= :orange-snake (:sprite-group s))
                        (qpsprite/set-animation s :tongue)
                        s))
                    sprites))))

(defn orange-stop-hiss
  [state]
  (update-in state [:scenes :intro :sprites]
             (fn [sprites]
               (map (fn [s]
                      (if (= :orange-snake (:sprite-group s))
                        (qpsprite/set-animation s :none)
                        s))
                    sprites))))

(defn laughing-tween
  []
  (qptween/->tween
   :pos
   20
   :step-count 10
   :update-fn (fn [[x y] d] [x (+ y d)])
   :yoyo? true
   :yoyo-update-fn (fn [[x y] d] [x (- y d)])
   :repeat-times 3))

(defn green-laugh
  [state]
  (qpsound/play "hiss/hiss2.wav")
  (update-in state [:scenes :intro :sprites]
             (fn [sprites]
               (map (fn [s]
                      (if (= :green-snake (:sprite-group s))
                        (-> s
                            (qptween/add-tween (laughing-tween))
                            (qpsprite/set-animation :tongue))
                        s))
                    sprites))))

(defn orange-laugh
  [state]
  (qpsound/play "hiss/hiss2.wav")
  (update-in state [:scenes :intro :sprites]
             (fn [sprites]
               (map (fn [s]
                      (if (= :orange-snake (:sprite-group s))
                        (-> s
                            (qptween/add-tween (laughing-tween))
                            (qpsprite/set-animation :tongue))
                        s))
                    sprites))))

(defn orange-turn-back
  [state]
  (update-in state [:scenes :intro :sprites]
             (fn [sprites]
               (map (fn [s]
                      (if (= :orange-snake (:sprite-group s))
                        (-> s
                            (assoc :spritesheet (q/load-image "img/snake/orange.png"))
                            (update :pos (fn [[x y]] [(+ x 150) y])))
                        s))
                    sprites))))

(defn orange-leave-right
  [state]
  (let [leaving-tween (qptween/->tween
                       :pos
                       500
                       :update-fn (fn [[x y] d] [(+ x d) y]))]
    (update-in state [:scenes :intro :sprites]
               (fn [sprites]
                 (map (fn [s]
                        (if (= :orange-snake (:sprite-group s))
                          (qptween/add-tween s leaving-tween)
                          s))
                      sprites)))))

(defn green-love
  [state]
  (let [pos [(* 0.17 (q/width)) (* 0.57 (q/height))]
        tween (qptween/->tween
               :pos
               20
               :step-count 50
               :update-fn (fn [[x y] d] [x (- y d)])
               :yoyo? true
               :yoyo-update-fn (fn [[x y] d] [x (+ y d)])
               :repeat-times ##Inf)
        heart (qptween/add-tween
               (qpsprite/image-sprite :heart pos 90 90 "img/cutscene/big-heart.png")
               tween)]
    (update-in state [:scenes :intro :sprites] conj heart)))

(defn love-end
  [state]
  (update-in state [:scenes :intro :sprites]
             (fn [sprites]
               (remove #(= :heart (:sprite-group %))
                       sprites))))

(defn bubble-1
  [state]
  (let [pos [(* 0.25 (q/width)) (* 0.52 (q/height))]
        bubble (qpsprite/image-sprite :bubble-1 pos 106 138 "img/cutscene/big-bubble-1.png")]
    (update-in state [:scenes :intro :sprites] conj bubble)))

(defn bubble-2
  [state]
  (let [pos [(* 0.4 (q/width)) (* 0.25 (q/height))]
        bubble (qpsprite/image-sprite :bubble-2 pos 322 346 "img/cutscene/big-bubble-2.png")]
    (update-in state [:scenes :intro :sprites] conj bubble)))

(defn present
  [state]
  (let [pos [(* 0.4 (q/width)) (* 0.25 (q/height))]
        present (qpsprite/image-sprite :present pos 322 346 "img/cutscene/big-present.png")]
    (update-in state [:scenes :intro :sprites]
               (fn [sprites]
                 (conj
                  (remove #(= :bubble-2 (:sprite-group %))
                          sprites)
                  present)))))

(defn finish
  [state]
  (qpscene/transition state :level-01
                      :transition-length 30
                      :init-fn (fn [state]
                                 state)))

(defn delays
  []
  (let [initial-delay 100
        delays [[0 green-pops-up]
                [160 green-hiss]
                [40 green-stop-hiss]
                [70 green-walk-right]
                [160 orange-pulled-left]
                [150 green-hiss]
                [40 green-stop-hiss]
                [40 orange-turn]
                [80 orange-hiss]
                [40 orange-stop-hiss]
                [60 green-hiss]
                [40 green-stop-hiss]
                [40 orange-hiss]
                [5 green-hiss]
                [40 orange-stop-hiss]
                [0 green-stop-hiss]
                [30 green-laugh]
                [0 orange-laugh]
                [40 green-stop-hiss]
                [0 orange-stop-hiss]
                [80 orange-hiss]
                [40 orange-stop-hiss]
                [60 orange-turn-back]
                [40 orange-leave-right]
                [160 green-love]
                [160 love-end]
                [60 bubble-1]
                [60 bubble-2]
                [80 present]
                [200 finish]
                ]]
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
  {:sprites []
   :delays (delays)
   :update-fn update-intro
   :draw-fn draw-intro
   ;; :key-pressed-fns [(fn [state e]
   ;;                     (when (= 32 (:key-code e))
   ;;                       (prn (:global-frame state)))
   ;;                     state)]
   ;; :mouse-pressed-fns [(fn [state e]
   ;;                       (prn ((juxt :x :y) e))
   ;;                       state)]
   })
