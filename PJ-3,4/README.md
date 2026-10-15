# Практическая работа №3

Типы данных, арифметика и корректный расчёт показателей

Завершённый проект рассчитывает базовую стоимость заказа, применяет процентную скидку, затем начисляет НДС. Реализован вариант 1: ставка НДС 10 %.

Сборка без Maven

```bash
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out ru.edu.pr01.SelfCheck
```

Основной класс: `ru.edu.pr01.OrderCalculator`. Граничные проверки находятся в `SelfCheck`.

```javac -encoding UTF-8 -d out src\ru\edu\pr01\OrderCalculator.java src\ru\edu\pr01\SelfCheck.java
```java -cp out ru.edu.pr01.SelfCheck   