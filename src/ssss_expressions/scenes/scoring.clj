(ns ssss-expressions.scenes.scoring
  (:require [quil.core :as q]
            [quip.collision :as qpcollision]
            [quip.sprites.button :as qpbutton]
            [quip.scene :as qpscene]
            [quip.sound :as qpsound]
            [quip.sprite :as qpsprite]
            [quip.tween :as qptween]
            [quip.utils :as qpu]
            [ssss-expressions.common :as common]
            [ssss-expressions.delay :as delay]
            [ssss-expressions.sprites.rat :as rat]
            [ssss-expressions.sprites.box :as box]))

(defn draw-scoring
  [state]
  (qpu/background common/grey)
  (-> state
      (qpscene/draw-scene-sprites)))

(defn sort-sprites
  [state]
  state)

(defn update-scoring
  [state]
  (-> state
      qpscene/update-scene-sprites
      delay/update-delays
      qptween/update-sprite-tweens
      common/handle-removal-flags
      qpcollision/update-collisions
      sort-sprites))

(defn sprites
  []
  [(box/box-body [(* 0.5 (q/width))
                  (* 0.6 (q/height))])
   (box/box-lid [(* 0.5 (q/width))
                 (* 0.4325 (q/height))])])

(defn rand-rat
  []
  (-> (rat/rat [(+ (- (* 0.5 (q/width)) 90)
                   (rand-int 180))
                (- (rand-int 250))])
      (qpsprite/set-animation :squished)
      (assoc :vel [0 7])
      (assoc :rotvel (- 5 (rand-int 10)))
      (assoc :update-fn qpsprite/update-animated-sprite)))

(defn rats
  [{:keys [current-scene scores prev-level] :as state}]
  (if (prev-level scores)
    (update-in state [:scenes current-scene :sprites]
               concat
               (take (prev-level scores)
                     (repeatedly rand-rat)))
    state))

(defn finish
  [{:keys [next-scene] :as state}]
  (qpscene/transition state next-scene
                      :transition-length 30
                      :init-fn (fn [state]
                                 (qpsound/stop-music)
                                 (qpsound/loop-music (case next-scene
                                                       :level-02 "music/Level_2.wav"
                                                       :level-03 "music/Level_3.wav"
                                                       "music/Chansssse_Encounter.wav"))
                                 (-> state
                                     (assoc :prev-level next-scene)))))

(defn delays
  []
  [(delay/->delay 50 box/open-box)
   (delay/->delay 100 rats)
   (delay/->delay 200 box/close-box)
   (delay/->delay 300 finish)])

(defn colliders
  []
  [(qpcollision/collider
    :rat
    :box-body
    (fn [a _]
      (common/flag-for-removal a))
    qpcollision/identity-collide-fn)])

(defn init
  []
  {:sprites (sprites)
   :delays (delays)
   :draw-fn draw-scoring
   :update-fn update-scoring
   :colliders (colliders)
   :mouse-pressed-fns [qpbutton/handle-buttons-pressed]
   :mouse-released-fns [qpbutton/handle-buttons-released]})
