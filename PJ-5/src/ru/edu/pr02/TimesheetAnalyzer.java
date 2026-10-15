package ru.edu.pr02;

public final class TimesheetAnalyzer {
    private static final int DAYS_IN_WEEK = 5;
    private static final int MAX_HOURS = 16;
    private static final int DAILY_NORMAL_HOURS = 8;
    private static final int WEEKLY_NORMAL_HOURS = 40;

    private TimesheetAnalyzer() {
    }

    public static int[] parseHours(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Ошибка: структура - строка не должна быть null");
        }

        String[] parts = line.split(";", -1);
        if (parts.length != DAYS_IN_WEEK) {
            throw new IllegalArgumentException(
                    "Ошибка: структура - ожидалось 5 значений, получено " + parts.length);
        }

        int[] result = new int[DAYS_IN_WEEK];
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (!part.matches("^0*[0-9]+$")) {
                throw new IllegalArgumentException(
                        "Ошибка: позиция " + (i + 1) + " - значение \"" + part
                                + "\" не является целым числом");
            }

            int value;
            try {
                value = Integer.parseInt(part);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Ошибка: позиция " + (i + 1) + " - значение \"" + part
                                + "\" вне диапазона [0, 16]");
            }
            if (value < 0 || value > MAX_HOURS) {
                throw new IllegalArgumentException(
                        "Ошибка: позиция " + (i + 1) + " - значение " + value
                                + " вне диапазона [0, 16]");
            }
            result[i] = value;
        }
        return result;
    }

    public static int total(int[] hours) {
        validateHours(hours);
        int sum = 0;
        for (int value : hours) {
            sum += value;
        }
        return sum;
    }

    public static double average(int[] hours) {
        validateHours(hours);
        double value = (double) total(hours) / DAYS_IN_WEEK;
        return Math.round(value * 100.0) / 100.0;
    }

    public static int overtimeDays(int[] hours) {
        validateHours(hours);
        int count = 0;
        for (int value : hours) {
            if (value > DAILY_NORMAL_HOURS) {
                count++;
            }
        }
        return count;
    }

    public static int weeklyOvertime(int[] hours) {
        validateHours(hours);
        return Math.max(0, total(hours) - WEEKLY_NORMAL_HOURS);
    }

    public static String addFormattedTimesheet(int[] hours) {
        validateHours(hours);
        String[] days = {"Пн", "Вт", "Ср", "Чт", "Пт"};
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < hours.length; i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(days[i]).append(':').append(hours[i]);
        }
        return result.toString();
    }

    private static void validateHours(int[] hours) {
        if (hours == null) {
            throw new IllegalArgumentException("hours is null");
        }
        if (hours.length != DAYS_IN_WEEK) {
            throw new IllegalArgumentException(
                    "hours length must be 5, got " + hours.length);
        }
    }
}
