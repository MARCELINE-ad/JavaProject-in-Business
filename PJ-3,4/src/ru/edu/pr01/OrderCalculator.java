package ru.edu.pr01;

import java.util.Scanner;

public class OrderCalculator {
    public static final double VAT_RATE = 10.0;
    private static final int MAX_QUANTITY = 10_000;
    private static final double MAX_UNIT_PRICE = 5_000_000.0;
    private static final double MAX_DISCOUNT_PERCENT = 30.0;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Количество: ");
            int quantity = scanner.nextInt();
            System.out.print("Цена за единицу: ");
            double unitPrice = scanner.nextDouble();
            System.out.print("Скидка, %: ");
            double discountPercent = scanner.nextDouble();

            if (!isValid(quantity, unitPrice, discountPercent)) {
                System.out.println("Ошибка: количество, цена или скидка вне допустимого диапазона.");
                return;
            }

            double base = calculateBase(quantity, unitPrice);
            double discounted = applyDiscount(base, discountPercent);
            double vat = calculateVat(discounted, VAT_RATE);
            double total = discounted + vat;
            System.out.printf("Базовая сумма: %.2f руб.%n", base);
            System.out.printf("После скидки: %.2f руб.%n", discounted);
            System.out.printf("НДС %.2f%%: %.2f руб.%n", VAT_RATE, vat);
            System.out.printf("Итого: %.2f руб.%n", total);
        } catch (java.util.InputMismatchException e) {
            System.out.println("Ошибка: введите числовые значения в правильном формате.");
        }
    }

    public static boolean isValid(int quantity, double unitPrice, double discountPercent) {
        return quantity > 0 && quantity <= MAX_QUANTITY
                && Double.isFinite(unitPrice)
                && unitPrice > 0.0 && unitPrice <= MAX_UNIT_PRICE
                && Double.isFinite(discountPercent)
                && discountPercent >= 0.0
                && discountPercent <= MAX_DISCOUNT_PERCENT;
    }
    public static double calculateBase(int quantity, double unitPrice) {
        return quantity * unitPrice;
    }
    public static double applyDiscount(double base, double discountPercent) {
        return base * (1.0 - discountPercent / 100.0);
    }
    public static double calculateVat(double discounted, double vatPercent) {
        return discounted * vatPercent / 100.0;
    }
    public static double calculateTotal(int quantity, double unitPrice, double discountPercent, double vatPercent) {
        if (!isValid(quantity, unitPrice, discountPercent)
                || !Double.isFinite(vatPercent) || vatPercent < 0.0) {
            throw new IllegalArgumentException("Некорректные входные данные");
        }
        double base = calculateBase(quantity, unitPrice);
        double discounted = applyDiscount(base, discountPercent);
        return discounted + calculateVat(discounted, vatPercent);
    }
}
