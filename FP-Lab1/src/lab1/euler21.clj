(ns lab1.euler21)

;; Быстрая версия sum-proper-divisors:
;; перебираем делители до sqrt(n), добавляем пары (i и n/i).
(defn sum-proper-divisors [n]
  (cond
    (<= n 1)
    0

    :else
    (let [sqrt-n (long (Math/floor (Math/sqrt n)))]
      (loop [i 2
             sum 1]
        (if (> i sqrt-n)
          sum
          (if (zero? (mod n i))
            (let [other (quot n i)]
              (recur (inc i)
                     (if (= other i)
                       (+ sum i)
                       (+ sum i other))))
            (recur (inc i) sum)))))))

;; --- Решения ---

;; 1. Монолитная реализация с хвостовой рекурсией
(defn solve-tail-rec []
  (loop [n 1
         total 0]
    (if (> n 9999)
      total
      (let [d-n     (sum-proper-divisors n)
            d-d-n   (sum-proper-divisors d-n)]
        (if (and (= d-d-n n)
                 (not= d-n n))
          (recur (inc n) (+ total n))
          (recur (inc n) total))))))

;; 2. Обычная рекурсивная версия (на самом деле тоже хвостовая — идентична 1)
(defn solve-rec []
  (loop [n 1
         total 0]
    (if (> n 9999)
      total
      (let [d-n     (sum-proper-divisors n)
            d-d-n   (sum-proper-divisors d-n)]
        (if (and (= d-d-n n)
                 (not= d-n n))
          (recur (inc n) (+ total n))
          (recur (inc n) total))))))

;; 3. Модульный/функциональный подход
(defn amicable? [n]
  (let [d-n   (sum-proper-divisors n)
        d-d-n (sum-proper-divisors d-n)]
    (and (= d-d-n n)
         (not= d-n n))))

(defn solve-modular []
  (->> (range 1 10000)
       (filter amicable?)
       (reduce + 0)))

;; 4. Использование for
(defn solve-for []
  (reduce +
          0
          (for [n (range 1 10000)
                :let [d-n   (sum-proper-divisors n)
                      d-d-n (sum-proper-divisors d-n)]
                :when (and (= d-d-n n)
                           (not= d-n n))]
            n)))

;; 5. Ленивые последовательности
(defn solve-lazy []
  (letfn [(amicable? [n]
            (let [d-n   (sum-proper-divisors n)
                  d-d-n (sum-proper-divisors d-n)]
              (and (= d-d-n n)
                   (not= d-n n))))

          (gen [n]
            (lazy-seq
             (cons n
                   (gen (inc n)))))]
    (->> (gen 1)
         (take-while #(< % 10000))
         (filter amicable?)
         (reduce + 0))))