package lab1;

public class Euler9 {

    // Оригинальный метод, оставляем (параметризованный по сумме)
    public static int findPythagoreanTripletProduct(int sum) {
        for (int a = 1; a < sum / 3; a++) {
            for (int b = a + 1; b < sum / 2; b++) {
                int c = sum - a - b;
                if (c > b && a * a + b * b == c * c) {
                    return a * b * c;
                }
            }
        }
        return -1;
    }

    // Метод, который ждёт Clojure: solve() без аргументов.
    public static int solve() {
        return findPythagoreanTripletProduct(1000);
    }
  

    // Удобный main для локальной проверки
    public static void main(String[] args) {
        int result = solve();
        System.out.println(result); // Ожидаемо: 31875000
    }
}
