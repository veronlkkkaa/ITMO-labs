(ns lab1.euler9)

;; вспомогательная функция для умножения коллекции (big-int friendly)
(defn product [coll]
  (reduce *' coll))

;; 1. Хвостовая рекурсия
(defn tail-rec []
  (loop [a 1
         b 2]
    (when-not (> a 333)
      (let [c (- 1000 a b)]
        (cond
          (> b c)
          (recur (inc a) (inc (inc a)))

          (= (+ (* a a)
                (* b b))
             (* c c))
          (* a b c)

          :else
          (recur a (inc b)))))))

;; 2. Обычная рекурсия
(defn solve-rec
  ([] (solve-rec 1))
  ([a]
   (when-not (> a 333)
     (let [res (some (fn [b]
                       (let [c (- 1000 a b)]
                         (when (and (> c b)
                                    (= (+ (* a a)
                                          (* b b))
                                       (* c c)))
                           (* a b c))))
                     (range (inc a) 500))]
       (or res (solve-rec (inc a)))))))

;; 3. Модульный подход
(defn all-triples []
  (for [a (range 1 334)
        b (range (inc a) 500)]
    [a b (- 1000 a b)]))

(defn pythagorean? [[a b c]]
  (and (> c b)
       (= (+ (* a a)
             (* b b))
          (* c c))))

(defn solve-modular []
  (->> (all-triples)
       (filter pythagorean?)
       first
       product))

;; 4. Генерация при помощи map
(def triples
  (mapcat (fn [a]
            (map (fn [b]
                   [a b (- 1000 a b)])
                 (range (inc a) 500)))
          (range 1 334)))

(defn solve-with-map []
  (->> triples
       (filter pythagorean?)
       first
       product))

;; 5. Бесконечная ленивная последовательность
(def lazy-triples
  (for [a (iterate inc 1)
        b (iterate inc (inc a))
        :let [c (- 1000 a b)]
        :while (pos? c)]
    [a b c]))

(defn solve-lazy []
  (->> lazy-triples
       (filter pythagorean?)
       first
       product))
