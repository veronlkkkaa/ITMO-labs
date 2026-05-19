(ns lab1.euler21-test
  (:require [clojure.test :refer [deftest is]]
            [lab1.euler21 :as euler21])
  (:import [lab1 Euler21]))

;; Эталонное значение из Java
(def ^:private java-answer (Euler21/solve))

;; Все реализации задачи 21
(def ^:private impls
  [["хвостовая рекурсия" euler21/solve-tail-rec]
   ["обычная рекурсия" euler21/solve-rec]
   ["модульная версия" euler21/solve-modular]
   ["через for" euler21/solve-for]
   ["ленивые вычисления" euler21/solve-lazy]])

(deftest all-euler21-implementations-agree-with-java
  (doseq [[desc f] impls]
    (is (= java-answer (f))
        (str "Реализация '" desc "' должна совпадать с Java-результатом"))))