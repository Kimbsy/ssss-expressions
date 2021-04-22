(ns ssss-expressions.scenes.outro
  (:require [quil.core :as q]
            [quip.scene :as qpscene]
            [quip.sprite :as qpsprite]
            [quip.sound :as qpsound]
            [quip.tween :as qptween]
            [quip.utils :as qpu]
            [ssss-expressions.common :as common]
            [ssss-expressions.delay :as delay]
            [ssss-expressions.sprites.box :as box]
            [ssss-expressions.sprites.rat :as rat]
            [ssss-expressions.sprites.snake :as snake]))

(defn update-outro
  [state]
  (-> state
      qpscene/update-scene-sprites
      qptween/update-sprite-tweens
      delay/update-delays))

(defn draw-outro
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
    (update-in state [:scenes :outro :sprites] conj snake)))

(defn green-pops-down
  [state]
  (let [tween (qptween/->tween
               :pos
               400
               :easing-fn qptween/exponential-easing-fn
               :update-fn common/tween-y-fn)]
    (update-in state [:scenes :outro :sprites]
               (fn [sprites]
                 (map (fn [s]
                        (if (= :green-snake (:sprite-group s))
                          (qptween/add-tween s tween)
                          s))
                      sprites)))))

(defn green-pops-back-up
  [state]
  (let [tween (qptween/->tween
               :pos
               -400
               :easing-fn qptween/asymptotic-easing-fn
               :update-fn common/tween-y-fn)]
    (update-in state [:scenes :outro :sprites]
               (fn [sprites]
                 (map (fn [s]/
                        (if (= :green-snake (:sprite-group s))
                          (qptween/add-tween s tween)
                          s))
                      sprites)))))

(defn green-hiss
  [state]
  (qpsound/play "hiss/hiss1.wav")
  (update-in state [:scenes :outro :sprites]
             (fn [sprites]
               (map (fn [s]
                      (if (= :green-snake (:sprite-group s))
                        (qpsprite/set-animation s :tongue)
                        s))
                    sprites))))

(defn green-stop-hiss
  [state]
  (update-in state [:scenes :outro :sprites]
             (fn [sprites]
               (map (fn [s]
                      (if (= :green-snake (:sprite-group s))
                        (qpsprite/set-animation s :none)
                        s))
                    sprites))))

(defn orange-pops-up
  [state]
  (let [pos [(* 0.8 (q/width)) (* 1.4 (q/height))]
        tween (qptween/->tween
               :pos
               400
               :easing-fn qptween/asymptotic-easing-fn
               :update-fn (fn [[x y] d]
                            [x (- y d)]))
        snake (qptween/add-tween
               (-> (snake/theatrical-snake :orange pos)
                   (qpsprite/set-animation :none-flipped))
               tween)]
    (update-in state [:scenes :outro :sprites] conj snake)))

(defn orange-hiss
  [state]
  (qpsound/play "hiss/hiss1.wav")
  (update-in state [:scenes :outro :sprites]
             (fn [sprites]
               (map (fn [s]
                      (if (= :orange-snake (:sprite-group s))
                        (qpsprite/set-animation s :tongue-flipped)
                        s))
                    sprites))))

(defn orange-stop-hiss
  [state]
  (update-in state [:scenes :outro :sprites]
             (fn [sprites]
               (map (fn [s]
                      (if (= :orange-snake (:sprite-group s))
                        (qpsprite/set-animation s :none-flipped)
                        s))
                    sprites))))

(defn present-pops-up
  [state]
  (let [pos-body [(* 0.45 (q/width)) (* 1.3 (q/height))]
        pos-lid [(* 0.45 (q/width)) (* (- 1.3 0.1675) (q/height))]
        tween (qptween/->tween
               :pos
               -400
               :easing-fn qptween/asymptotic-easing-fn
               :update-fn common/tween-y-fn)
        box-body (qptween/add-tween
                  (box/box-body pos-body)
                  tween)
        box-lid (qptween/add-tween
                 (box/box-lid pos-lid)
                 tween)]
    (update-in state [:scenes :outro :sprites] conj box-body box-lid)))

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
    (update-in state [:scenes :outro :sprites] conj heart)))

(defn orange-love
  [state]
  (let [pos [(* 0.83 (q/width)) (* 0.57 (q/height))]
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
    (update-in state [:scenes :outro :sprites] conj heart)))

(defn gravity-update-rat
  [r]
  (-> r
      (update :vel (fn [[x y]] [x (+ y 0.1)]))
      rat/update-rat))

(defn rat-fountain
  [{:keys [scores delays] :as state}]
  (let [total-score (reduce + (vals scores))
        new-delays  (for [i (range total-score)]
                      (delay/add-sprites-to-scene-delay
                       (* 3 i)
                       [(-> (rat/rat [(* 0.45 (q/width)) (* 0.67 (q/height))])
                            (assoc :vel [(- (* 3 (rand)) 1.5)
                                         (- (* 5 (rand)) 8)])
                            (assoc :current-animation :squished)
                            (assoc :update-fn gravity-update-rat))]))]
    (update-in state [:scenes :outro :delays] concat new-delays)))

(defn finish
  [state]
  (qpscene/transition state :credits
                      :transition-length 80
                      :init-fn (fn [state]
                                 (qpsound/stop-music)
                                 (qpsound/loop-music "music/Romantic_and_Triumphant_Victory.wav")
                                 state)))

(defn delays
  []
  (let [initial-delay 100
        delays [[0 green-pops-up]
                [160 green-hiss]
                [40 green-stop-hiss]
                [80 orange-pops-up]
                [120 orange-hiss]
                [40 orange-stop-hiss]
                [80 green-hiss]
                [40 green-stop-hiss]
                [20 green-pops-down]
                [120 green-pops-back-up]
                [0 present-pops-up]
                [160 box/open-box]
                [80 rat-fountain]
                [260 box/close-box]
                [140 orange-love]
                [80 green-love]
                [230 finish]]]
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
   :update-fn update-outro
   :draw-fn draw-outro})
