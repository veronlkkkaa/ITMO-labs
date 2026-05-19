(ns lab1.euler9-test
  (:require [clojure.test :refer [deftest is]]
            [lab1.euler9 :as euler9])
  (:import [lab1 Euler9]))

;; Эталонное значение из Java
(def ^:private java-answer (Euler9/solve))

;; Список всех реализаций как пар [название функция]
(def ^:private impls
  [["хвостовая рекурсия" euler9/tail-rec]
   ["обычная рекурсия" euler9/solve-rec]
   ["модульная версия" euler9/solve-modular]
   ["через map" euler9/solve-with-map]
   ["ленивые вычисления" euler9/solve-lazy]])

(deftest all-euler9-implementations-agree-with-java
  (doseq [[desc f] impls]
    (is (= java-answer (f))
        (str "Реализация '" desc "' должна совпадать с Java-результатом"))))