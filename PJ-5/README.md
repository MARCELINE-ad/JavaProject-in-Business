# Практическая работа №5 — вариант 9

Проект анализирует табель из пяти значений часов, вычисляет сумму, среднее, число дней с переработкой и недельную переработку. В варианте 9 разрешены ведущие нули, а метод `addFormattedTimesheet` формирует человекочитаемую строку табеля.

## Требования

- Java SE 21 или новее
- кодировка UTF-8
- Maven и внешние библиотеки не требуются

## Сборка и проверка

### Windows

```bat
run_selfcheck.bat
```

### Linux или macOS

```bash
chmod +x run_selfcheck.sh
./run_selfcheck.sh
```

Ручная сборка:

```bash
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out ru.edu.pr02.SelfCheck
```

Для интерактивного ввода:

```bash
java -cp out ru.edu.pr02.SelfCheck --interactive
```

Введите строку, например `08;9;10;8;8`. Команды `exit` или пустая строка завершают режим.
