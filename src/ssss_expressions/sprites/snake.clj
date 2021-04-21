(ns ssss-expressions.sprites.snake
  (:require [quil.core :as q]
            [quip.sprite :as qpsprite]
            [quip.utils :as qpu]
            [ssss-expressions.common :as common]))

(def initial-body
  '((110.23890371560427 446.7461036556622)
    (121.1845712155747 429.6378805607759)
    (118.84874277283633 408.8266314585204)
    (105.05926029719579 393.8271339457432)
    (86.74998716545065 383.0022935866722)
    (70.94842031178877 369.668373404456)
    (62.53460529797476 351.25108938398733)
    (68.14615585279142 331.32747981757234)
    (86.77029410175673 322.7495992676651)
    (108.17731771205206 323.82195765327765)
    (128.86925921579788 321.68174391461207)
    (146.3751877306892 311.28059810888476)
    (153.23119274421845 291.6712050515573)
    (142.9077634688996 273.92711835536306)
    (122.29432227169124 272.82639890532187)
    (102.6156688379157 276.6462432960525)
    (82.59265801290685 276.76050783455116)
    (64.12215374367409 266.73660284734154)
    (56.78385886348623 247.31582809049536)
    (66.86614141495696 229.66604684815013)
    (87.19248021791361 226.27486613703604)))

(defn body-intersects-w-h-rect
  [{:keys [body pos]} {[x y] :pos}]
  (let [smaller-rect {:pos [(- x 10) (- y 10)] :w 15 :h 15}]
    (some
     #(common/line-intersects-rect? % smaller-rect)
     (partition 2 1 (conj body pos)))))

(defn update-vel
  [{:keys [rotation vel] :as s}]
  (assoc s :vel (map #(* (qpu/magnitude vel) %)
                     (qpu/direction-vector rotation))))


(defn update-body
  [{:keys [body pos] :as s}]
  (let [p0 (last body) ; the snake shoulders
        dv (map - pos p0) ; delta between shoulders and head
        p-new (map + p0 (map #(/ % 2) dv))]
    (if (< 40 (qpu/magnitude dv))
      (update s :body  (fn [ps]
                         (pop (conj ps p-new))))
      s)))

(defn update-player-snake
  [s {:keys [held-keys]}]
  (if (or (held-keys :right)
          (held-keys :left))
    (-> s
        (qpsprite/update-pos)
        update-vel
        update-body)
    s))

(defn project-point
  [{[x y :as pos] :pos
    :keys [rotation]}]
  (map + pos (map #(* 80 %) (qpu/direction-vector rotation))))

(defn draw-debug
  [{[x y :as pos] :pos
    :keys [rotation]
    :as s}]
  (qpu/stroke [0 0 255])
  (q/stroke-weight 2)
  (apply q/line x y (project-point s)))

(defn draw-player-snake
  [{[x y :as pos] :pos
    :keys [body
           rotation]
    :as s}]
  (qpu/stroke common/light-green)

  ;; draw the body
  (q/no-fill)
  (q/stroke-weight 5)
  (q/begin-shape)
  (apply q/curve-vertex (peek body))
  (doseq [p body]
    (apply q/curve-vertex p))
  (apply q/curve-vertex pos)
  (apply q/curve-vertex pos)
  (q/end-shape)

  ;; draw the head
  (q/stroke-weight 1)
  (qpu/fill common/light-green)
  (qpu/wrap-trans-rot
   pos rotation
   (fn []
     (q/rect -5 0 10 -20 5)
     (qpu/stroke common/grey)
     (qpu/fill common/grey)
     (q/rect 2 -14 2 2)
     (q/rect -4 -14 2 2)))

  ;; draw the body segments
  ;; (qpu/stroke qpu/red)
  ;; (doseq [[a b] (partition 2 1 (conj body pos))]
  ;;   (q/line a b))
  )

(defn player-snake
  [[x y :as pos]]
  {:sprite-group :player-snake
   :uuid (java.util.UUID/randomUUID)
   :pos pos
   :vel [0 3] ; apparently need some initial velocity, whatevs
   :rotation 90 ; start facing right
   :points []
   :update-fn update-player-snake
   :draw-fn draw-player-snake
   :bounds-fn (constantly false) ; ... this is gonna take some thinking
   :body (apply conj clojure.lang.PersistentQueue/EMPTY
                initial-body)})

(defn theatrical-snake
  [color pos &
   {:keys [rotation current-animation]
    :or {rotation 0
         current-animation :none}}]
  (qpsprite/animated-sprite
   (keyword (str (name color) "-snake"))
   pos
   192
   384
   (str "img/snake/" (name color) ".png")
   :animations {:none   {:frames      1
                         :y-offset    0
                         :frame-delay 100}
                :tongue {:frames 5
                         :y-offset 1
                         :frame-delay 4}
                :none-flipped   {:frames      1
                                 :y-offset    2
                                 :frame-delay 100}
                :tongue-flipped {:frames 5
                                 :y-offset 3
                                 :frame-delay 4}}
   :current-animation current-animation
   :rotation rotation))

(defn update-snake
  [{:keys [current-scene] :as state} f]
  (let [sprites (-> state :scenes current-scene :sprites)
        snake (first (filter #(= :player-snake (:sprite-group %)) sprites))
        non-snakes (remove #(= :player-snake (:sprite-group %)) sprites)]
    (assoc-in state [:scenes current-scene :sprites] (conj non-snakes (f snake)))))

(defn handle-direction-input
  [{:keys [held-keys] :as state}]
  (if (seq held-keys)
    (cond
      (held-keys :right)
      (update-snake state (fn [s]
                            (update s :rotation #(+ % 5))))
      (held-keys :left)
      (update-snake state (fn [s]
                            (update s :rotation #(- % 5))))
      :else
      state)
    state))
