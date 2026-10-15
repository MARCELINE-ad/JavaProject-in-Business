package ru.edu.pr01;
public class SelfCheck {
    private static int passed;
    private static int total;

    public static void main(String[] args) {
        check("типовое значение", close(OrderCalculator.calculateTotal(2, 100.0, 10.0, 10.0), 198.0));
        check("минимальное количество", OrderCalculator.isValid(1, 0.01, 0.0));
        check("максимальные границы", OrderCalculator.isValid(10_000, 5_000_000.0, 30.0));
        check("нулевое количество", !OrderCalculator.isValid(0, 100.0, 10.0));
        check("цена выше максимума", !OrderCalculator.isValid(1, 5_000_000.01, 10.0));
        check("скидка выше максимума", !OrderCalculator.isValid(1, 100.0, 30.01));
        check("NaN в цене", !OrderCalculator.isValid(1, Double.NaN, 10.0));
        check("НДС после скидки", close(OrderCalculator.calculateTotal(12, 2490.50, 5.0, OrderCalculator.VAT_RATE), 31230.87));
        System.out.printf("Итог: %d/%d проверок пройдено%n", passed, total);
        if (passed != total) System.exit(1);
    }

    private static void check(String name, boolean condition) {
        total++;
        if (condition) passed++;
        System.out.println((condition ? "PASS " : "FAIL ") + name);
    }

    private static boolean close(double actual, double expected) {
        return Math.abs(actual - expected) < 0.005;
    }
}
