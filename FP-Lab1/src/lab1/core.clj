(ns lab1.core
  (:gen-class)
  (:require [lab1.euler9 :as euler9]
            [lab1.euler21 :as euler21])
  (:import [lab1 Euler9 Euler21]))

;(defn- check-result
;  [expected actual]
;  (if (= expected actual)
;    (str actual " ✓")
;    (str actual " ✗ (ожидалось " expected ")")))

;; безопасный вызов с таймингом и обработкой исключений
(defn safe-call [label f]
  (println label)
  (try
    (let [start (System/nanoTime)
          res (f)
          ms (/ (double (- (System/nanoTime) start)) 1e6)]
      (println "  result:" res)
      (println (format "  elapsed: %.2f ms" ms))
      res)
    (catch Throwable e
      (println "  ERROR:" (.getMessage e))
      (.printStackTrace e)
      nil)))

(defn -main
  []
  ;; получаем эталонные результаты из Java (они вычисляются сразу)
  (let [euler9-java-result
        (try
          (Euler9/solve)
          (catch Throwable e
            (println "Error calling Euler9/solve:" (.getMessage e))
            nil))
        euler21-java-result
        (try
          (Euler21/solve)
          (catch Throwable e
            (println "Error calling Euler21/solve:" (.getMessage e))
            nil))]

    (println "---")
    (println "ЗАДАЧА 9 (Пифагорова тройка)")
    (println "---")
    (println "Эталонный ответ (Java):" euler9-java-result)
    (println "\nРезультаты Clojure реализаций:")

    ;; вызываем по одной реализации и печатаем результат/время/ошибку
    (safe-call "1. Хвостовая рекурсия:" #(euler9/tail-rec))
    (safe-call "2. Обычная рекурсия:" #(euler9/solve-rec))
    (safe-call "3. Модульная:" #(euler9/solve-modular))
    (safe-call "4. Генерация через map:" #(euler9/solve-with-map))
    (safe-call "5. Ленивые последовательности:" #(euler9/solve-lazy))

    (println "\n---")
    (println "ЗАДАЧА 21 (Дружественные числа)")
    (println "---")
    (println "Эталонный ответ (Java):" euler21-java-result)
    (println "\nРезультаты Clojure реализаций:")

    (safe-call "1. Хвостовая рекурсия:" #(euler21/solve-tail-rec))
    (safe-call "2. Обычная рекурсия:" #(euler21/solve-rec))
    (safe-call "3. Модульная:" #(euler21/solve-modular))
    (safe-call "4. Спец. синтаксис for:" #(euler21/solve-for))
    (safe-call "5. Ленивые последовательности:" #(euler21/solve-lazy)))

  (System/exit 0))
