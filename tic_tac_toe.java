/*
    КРЕСТИКИ-НОЛИКИ
    Александр Степанов (с) 06.02.2020
 */

import java.util.Scanner;

public class krestnol {
    private static int table_dim = 3;     // Размеры игрового поля (9 - предел, обусловленный вводом значений)
    private static int[][] table = new int[table_dim][table_dim]; // Само игровое поле
    private static int PC_PlayBy = 1;     // ПК играет за... 1=крестики (ходят первыми), 100=нолики

    // Вывод игрового поля на экран
    private static void printGameTable() {
        // Вывод верхней строки цифр (по оси Х)
        System.out.print("\n  ");
        for (int x = 0; x < table_dim; x++) System.out.print("   " + x);
        // Формирование горизонтальной линии сетки
        StringBuilder line = new StringBuilder("\n   +");
        line.append("---+".repeat(table_dim));
        line.append("\n");
        // Рисуем саму сетку
        for (int y = 0; y < table_dim; y++) {
            System.out.print(line);
            System.out.print(" " + y + " |");
            for (int x = 0; x < table_dim; x++) {
                if (table[y][x] > 0) {
                    if (table[y][x] == 1) System.out.print(" X |");
                    else System.out.print(" O |");
                } else System.out.print("   |");
            }
        }
        System.out.println(line);
    }

    // Ход пользователя
    public static void UserTurn() {
        String input;
        int x = 0, y = 0;
        Scanner in = new Scanner(System.in);
        repeat_input:
        while (true) {
            System.out.print("\nВведите координаты в формате YX (строка, столбец): ");
            input = in.nextLine();
            if (input.length() != 2) {
                System.out.println("Вы должны ввести слитно два числа!\n");
                continue repeat_input;
            }
            for (int i = 0; i<2; i++) {
                if ((input.charAt(i) < '0') | (input.charAt(i) > '0' + table_dim)) {
                    System.out.printf("Значение %c вне допустимого диапазона! (введено %c, а должно быть от 0 до %d)\n", i == 0 ? 'Y' : 'X', input.charAt(i), table_dim - 1);
                    continue repeat_input;
                }
                if (i == 0) y = input.charAt(i) - '0';
                else {
                    x = input.charAt(i) - '0';
                    if (table[y][x] != 0) {
                        System.out.println("Указанная позиция уже занята. Введите другие координаты!");
                        continue repeat_input;
                    }
                    break repeat_input;
                }
            }
        }
        table[y][x] = (PC_PlayBy == 1) ? 100 : 1;  // Ход игрока (нолики, если у компа крестики и наоборот)
    }

    /* Анализ игрового поля на предмет победы
       out: 0 - никто пока не выиграл (игра продолжается)
            1 - выиграли "крестики"
            100 - выиграли "нолики"
            200 - ничья */
    public static int isWin() {
        int sum;
        // Анализируем по строкам (по горизонтали)
        for (int y = 0; y < table_dim; y++) {
            sum = 0;
            for (int val : table[y]) sum += val;
            if (sum == table_dim * 100) return 100;
            if (sum == table_dim) return 1;
        }
        // Анализируем по столбцам (по вертикали)
        for (int x = 0; x < table_dim; x++) {
            sum = 0;
            for (int y = 0; y < table_dim; y++) sum += table[y][x];
            if (sum == table_dim * 100) return 100;
            if (sum == table_dim) return 1;
        }
        // Анализируем диагональ левый верхний угол - правый нижний
        sum = 0;
        for (int x = 0; x < table_dim; x++) sum += table[x][x];
        if (sum == table_dim * 100) return 100;
        if (sum == table_dim) return 1;
        // Анализируем диагональ левый нижний угол - правый верхний
        sum = 0;
        for (int y = table_dim - 1, x = 0; y >= 0; y--, x++) sum += table[y][x];
        if (sum == table_dim * 100) return 100;
        if (sum == table_dim) return 1;
        // Проверка на ничью (все клетки заняты - нет доступных ходов)
        sum = 0;
        for (int y = 0; y < table_dim; y++) {
            for (int val : table[y]) if (val != 0) sum++;
        }
        if (sum == table_dim * table_dim) return 200;
        // Игра продолжается...
        return 0;
    }

    /* Проверка на выигрыш с выводом соответствующего сообщения
       out: возвращаемое значение смотри isWin() */
    private static int isWinWithMessage() {
        int ret_val = isWin();
        switch (isWin()) {
            case 1:
                System.out.println("Победили крестики!");
                return ret_val;
            case 100:
                System.out.println("Победили нолики!");
                return ret_val;
            case 200:
                System.out.println("Ничья!");
                return ret_val;
            default:
                return ret_val;
        }
    }

    /* Определение веса на основании суммы существующих ходов в ряду
       input: сумма в ряду
       out: вычисленный вес для этой суммы */
    private static int getWeightBySum(int sum) {
        int weight = 1;
        if (PC_PlayBy == 1) { //Комп ходит крестиками
            if (sum == (table_dim - 1)) weight += 1000; // Это наш выигрышный ход (соберется ряд)
            if (sum == 100 * (table_dim - 1)) weight += 100; // Защита, мешаем собрать ряд противнику
            if (sum >= 100) weight += 10;   // У противника уже есть клетка в этом ряду
            if (sum % 100 > 0) weight += 5; // У нас уже есть клетка в этом ряду
        } else {  //Комп ходит ноликами
            if (sum == 100 * (table_dim - 1)) weight += 1000;  // Это наш выигрышный ход (соберется ряд)
            if (sum == (table_dim - 1)) weight += 100; // Защита, мешаем собрать ряд противнику
            if (sum % 100 > 0) weight += 10; // У противника уже есть клетка в этом ряду
            if (sum >= 100) weight += 5;     // У нас уже есть клетка в этом ряду
        }
        return weight;
    }

    /* Получить вес клетки по координатам для хода компа
       input: y, x - координаты по соответствующим осям
       out: совокупный вес клетки для этих координат */
    private static int getWeight(int _y, int _x) {
        int sum, weight = 0;
        // увеличим вес угловой клетки, так как это более выгодная позиция
        if ((_x == 0 & _y == 0) | (_x == table_dim - 1 & _y == table_dim - 1) | (_x == 0 & _y == table_dim - 1) | (_x == table_dim - 1 & _y == 0))
            weight += 5;
        // Центральную клетку занимаем первым ходом!
        if (_y == table_dim / 2 & _x == table_dim / 2) weight += 5000;
        // Проверка по горизонтали
        sum = 0;
        for (int val : table[_y]) sum += val;
        weight += getWeightBySum(sum);
        // Проверка по вертикали
        sum = 0;
        for (int y = 0; y < table_dim; y++) sum += table[y][_x];
        weight += getWeightBySum(sum);
        // Проверка по диагонали верхний левый угол - правый нижний
        sum = 0;
        if ((_x == 0 & _y == 0) || (_x == table_dim - 1 & _y == table_dim - 1)) {
            for (int x = 0; x < table_dim; x++) sum += table[x][x];
            // Уменьшим вес, если центр наш и по диагонали клетка занята противником (нам нет смысла от нее защищаться)
            if ((table[table_dim / 2][table_dim / 2] == PC_PlayBy) & (sum - PC_PlayBy > 0)) weight += getWeightBySum(sum) - 5;
            else weight += getWeightBySum(sum);
        }
        // Проверка по диагонали нижний левый угол - правый верхний
        sum = 0;
        if ((_y == table_dim - 1 & _x == 0) | (_y == 0 & _x == table_dim - 1)) {
            for (int y = table_dim - 1, x = 0; y >= 0; y--, x++) sum += table[y][x];
            // Уменьшим вес, если центр наш и по диагонали клетка занята противником (нам нет смысла от нее защищаться)
            if ((table[table_dim / 2][table_dim / 2] == PC_PlayBy) & (sum - PC_PlayBy > 0)) weight += getWeightBySum(sum) - 5;
            else weight += getWeightBySum(sum);
        }
        return weight;
    }

    // Ход компа
    public static void PcTurn() {
        int max_weight = 0;         // Максимальный вес, служит для оценки эффективности хода
        int best_x = 0, best_y = 0; // Координаты оптимального хода (с максимальным весом)
        int weight;
        // Ищем свободные клетки и определяем для них веса, попутно ищем максимальный вес и его координаты
        for (int y = 0; y < table_dim; y++) {
            for (int x = 0; x < table_dim; x++) {
                if (table[y][x] != 0) continue; // Клетка не пуста
                weight = getWeight(y, x);
                if (max_weight < weight) {
                    max_weight = weight;
                    best_x = x;
                    best_y = y;
                }
                //System.out.printf("\nDebug weights: Y=%d, X=%d, weight=%d", y, x, weight);
            }
        }
        table[best_y][best_x] = PC_PlayBy; // Делаем ход на основании найденных оптимальных координат
    }

    public static void main(String[] args) {
        if (PC_PlayBy == 1) PcTurn(); // Если комп играет крестиками, то ходит первым
        printGameTable();
        while (true) {
            UserTurn();
            printGameTable();
            if (isWinWithMessage() != 0) return;
            PcTurn();
            printGameTable();
            if (isWinWithMessage() != 0) return;
        }
    }
}
