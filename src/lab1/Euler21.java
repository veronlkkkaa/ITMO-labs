package lab1;

public class Euler21 {

    // Вспомогательная функция: сумма собственных делителей
    public static int sumProperDivisors(int n) {
        if (n <= 1) return 0;
        int sum = 1;
        for (int i = 2; i <= n / 2; i++) {
            if (n % i == 0) {
                sum += i;
            }
        }
        return sum;
    }

    // Существующий (параметризованный) метод
    public static int findSumOfAmicableNumbers(int limit) {
        int total = 0;
        for (int n = 1; n < limit; n++) {
            int dN = sumProperDivisors(n);
            if (dN != n && dN > 0 && sumProperDivisors(dN) == n) {
                total += n;
            }
        }
        return total;
    }

    // Метод, который ждёт Clojure: solve() без аргументов
    public static int solve() {
        return findSumOfAmicableNumbers(10000);
    }

    // Удобный main для локальной проверки
    public static void main(String[] args) {
        int sum = solve();
        System.out.println(sum); // Ожидаемо: 31626
    }
}
