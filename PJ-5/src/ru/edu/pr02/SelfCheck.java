package ru.edu.pr02;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public final class SelfCheck {
    private static int passed;
    private static int totalChecks;

    private SelfCheck() { }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        runUnitChecks();
        System.out.printf("Unit-проверки: %d/%d passed%n", passed, totalChecks);
        runDataDriven(Path.of("data", "sample.csv"));
        if (Arrays.asList(args).contains("--interactive")) {
            runInteractive();
        } else {
            System.out.println("Интерактивный режим: добавьте аргумент --interactive");
        }
    }

    private static void runUnitChecks() {
        check("parseHours: корректная строка", Arrays.equals(
                TimesheetAnalyzer.parseHours("8;9;10;8;8"), a(8, 9, 10, 8, 8)));
        checkParseException("parseHours: пустая строка", "", "структура");
        checkParseException("parseHours: значение 17", "8;8;17;8;8", "позиция 3");
        checkParseException("parseHours: не число", "8;abc;8;8;8", "позиция 2");
        checkParseException("parseHours: четыре значения", "8;8;8;8", "структура");
        checkParseException("parseHours: trailing-разделитель", "8;8;8;8;8;", "структура");
        check("parseHours: ведущий ноль варианта 9",
                TimesheetAnalyzer.parseHours("08;8;8;8;8")[0] == 8);
        check("total: типовой", TimesheetAnalyzer.total(a(8, 9, 10, 8, 8)) == 43);
        check("total: нижняя граница", TimesheetAnalyzer.total(a(0, 0, 0, 0, 0)) == 0);
        check("total: верхняя граница", TimesheetAnalyzer.total(a(16, 16, 16, 16, 16)) == 80);
        check("average: типовой", eq(TimesheetAnalyzer.average(a(8, 9, 10, 8, 8)), 8.6));
        check("average: целое", eq(TimesheetAnalyzer.average(a(8, 8, 8, 8, 8)), 8.0));
        check("average: дробное", eq(TimesheetAnalyzer.average(a(7, 8, 9, 8, 7)), 7.8));
        check("overtimeDays: нет переработки", TimesheetAnalyzer.overtimeDays(a(8, 8, 8, 8, 8)) == 0);
        check("overtimeDays: два дня", TimesheetAnalyzer.overtimeDays(a(9, 8, 7, 10, 8)) == 2);
        check("overtimeDays: граница 8", TimesheetAnalyzer.overtimeDays(a(8, 9, 8, 8, 8)) == 1);
        check("weeklyOvertime: сумма 40", TimesheetAnalyzer.weeklyOvertime(a(8, 8, 8, 8, 8)) == 0);
        check("weeklyOvertime: сумма 50", TimesheetAnalyzer.weeklyOvertime(a(10, 10, 10, 10, 10)) == 10);
        check("weeklyOvertime: сумма меньше 40", TimesheetAnalyzer.weeklyOvertime(a(6, 6, 6, 6, 6)) == 0);
        check("вариант 9A: несколько ведущих нулей", Arrays.equals(
                TimesheetAnalyzer.parseHours("000;016;09;8;01"), a(0, 16, 9, 8, 1)));
        checkParseException("вариант 9A: только нули и цифры", "+08;8;8;8;8", "позиция 1");
        check("вариант 9Б: формат типового табеля",
                TimesheetAnalyzer.addFormattedTimesheet(a(8, 9, 10, 8, 8))
                        .equals("Пн:8, Вт:9, Ср:10, Чт:8, Пт:8"));
        check("вариант 9Б: формат нулевой недели",
                TimesheetAnalyzer.addFormattedTimesheet(a(0, 0, 0, 0, 0))
                        .equals("Пн:0, Вт:0, Ср:0, Чт:0, Пт:0"));
        checkTotalNullException();
        checkAverageLengthException();
    }

    private static void checkParseException(String name, String input, String messagePart) {
        try {
            TimesheetAnalyzer.parseHours(input);
            check(name, false);
        } catch (IllegalArgumentException e) {
            check(name, e.getMessage().contains(messagePart));
        }
    }

    private static void checkTotalNullException() {
        try {
            TimesheetAnalyzer.total(null);
            check("защитное условие: null", false);
        } catch (IllegalArgumentException e) {
            check("защитное условие: null", e.getMessage().contains("hours is null"));
        }
    }

    private static void checkAverageLengthException() {
        try {
            TimesheetAnalyzer.average(new int[]{8, 8, 8});
            check("защитное условие: длина массива", false);
        } catch (IllegalArgumentException e) {
            check("защитное условие: длина массива", e.getMessage().contains("length must be 5"));
        }
    }

    private static void runDataDriven(Path file) {
        int dataPassed = 0;
        int dataTotal = 0;
        try {
            List<String> lines = Files.readAllLines(file);
            for (String row : lines) {
                if (row.isBlank() || row.startsWith("#")) continue;
                dataTotal++;
                String[] columns = row.split("\\|", 3);
                if (columns.length != 3) {
                    System.out.println("FAIL data/" + dataTotal + ": неверный формат строки");
                    continue;
                }
                String input = columns[0].equals("EMPTY") ? "" : columns[0];
                boolean ok = verifyDataCase(input, columns[1]);
                System.out.printf("%s data/%d: %s%n", ok ? "PASS" : "FAIL", dataTotal, columns[2]);
                if (ok) dataPassed++;
            }
        } catch (IOException e) {
            System.out.println("FAIL data: " + e.getMessage());
        }
        System.out.printf("Data-driven: %d/%d passed%n", dataPassed, dataTotal);
    }

    private static boolean verifyDataCase(String input, String expected) {
        try {
            int[] hours = TimesheetAnalyzer.parseHours(input);
            if (!expected.startsWith("OK:")) return false;
            String body = expected.substring(3);
            String expectedFormat = null;
            int formatIndex = body.indexOf(",fmt=");
            if (formatIndex >= 0) {
                expectedFormat = body.substring(formatIndex + 5);
                body = body.substring(0, formatIndex);
            }
            String[] metrics = body.split(",");
            int sum = Integer.parseInt(metrics[0].substring(4));
            double avg = Double.parseDouble(metrics[1].substring(4));
            int od = Integer.parseInt(metrics[2].substring(3));
            int wo = Integer.parseInt(metrics[3].substring(3));
            boolean metricsOk = TimesheetAnalyzer.total(hours) == sum
                    && eq(TimesheetAnalyzer.average(hours), avg)
                    && TimesheetAnalyzer.overtimeDays(hours) == od
                    && TimesheetAnalyzer.weeklyOvertime(hours) == wo;
            return metricsOk && (expectedFormat == null
                    || TimesheetAnalyzer.addFormattedTimesheet(hours).equals(expectedFormat));
        } catch (IllegalArgumentException e) {
            return expected.startsWith("ERROR:") && e.getMessage().contains(expected.substring(6));
        }
    }

    private static void runInteractive() {
        System.out.println("Введите табель или exit. Пустая строка завершает работу.");
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isEmpty() || line.equalsIgnoreCase("exit")) break;
                try {
                    int[] hours = TimesheetAnalyzer.parseHours(line);
                    System.out.println("Сумма: " + TimesheetAnalyzer.total(hours));
                    System.out.printf("Среднее: %.2f%n", TimesheetAnalyzer.average(hours));
                    System.out.println("Дней с переработкой: " + TimesheetAnalyzer.overtimeDays(hours));
                    System.out.println("Недельная переработка: " + TimesheetAnalyzer.weeklyOvertime(hours));
                    System.out.println("Табель: " + TimesheetAnalyzer.addFormattedTimesheet(hours));
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private static void check(String name, boolean ok) {
        totalChecks++;
        if (ok) {
            passed++;
            System.out.println("PASS " + name);
        } else {
            System.out.println("FAIL " + name);
        }
    }

    private static int[] a(int... values) { return values; }

    private static boolean eq(double a, double b) {
        return Math.abs(a - b) < 0.000001;
    }
}
