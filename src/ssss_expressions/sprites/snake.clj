(ns ssss-expressions.sprites.snake
  (:require [quil.core :as q]
            [quip.sprite :as qpsprite]
            [quip.utils :as qpu]
            [ssss-expressions.common :as common]))

(defn update-vel
  [{:keys [rotation vel] :as s}]
  (assoc s :vel (map #(* (qpu/magnitude vel) %)
                     (qpu/direction-vector rotation))))


;; @TODO: when we're not turning we shouldn't add new body vertices, just move the last one
(defn update-body
  [{:keys [body pos] :as s}]
  (update s :body conj pos))

(defn update-player-snake
  [s]
  (-> s
      (qpsprite/update-pos)
      update-vel
      update-body))

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
  [{[x y] :pos
    :keys [body]
    :as s}]
  (qpu/stroke common/light-green)
  (q/no-fill)
  (q/stroke-weight 5)
  (q/begin-shape)
  (apply q/curve-vertex (first body))
  (doseq [p body]
    (apply q/curve-vertex p))
  (apply q/curve-vertex (project-point s))
  (q/end-shape)


  (draw-debug s)
  )

(defn player-snake
  [[x y :as pos]]
  {:sprite-group :player-snake
   :uuid (java.util.UUID/randomUUID)
   :pos pos
   :vel [3 0]
   :rotation 90 ; start facing right
   :points []
   :update-fn update-player-snake
   :draw-fn draw-player-snake
   :bounds-fn (constantly false) ; ... this is gonna take some thinking
   :body [[(- x 150) (- y 80)]
          [(- x 80) (- y 40)]
          [(- x 40) (- y 80)]
          [(- x 40) (- y 40)]
          pos]})

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
