package ru.mfti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Arrays;
import java.util.Stack;


//REST API
@RestController
class SummatorController {

    @GetMapping("/make")
    public String arithmeticExpression(String expression) {
        return fun(expression);
    }

    //логику писать сюда
    public static String fun(String inpStr) {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();
        int bracketsCount = 0;

        // удаляем из исходной строки все пробелы, а также запятые заменяю точками
        String str = inpStr.replaceAll(" ", "").replaceAll(",", ".");


        CharacterIterator it = new StringCharacterIterator(str);
        StringBuilder token = new StringBuilder();
        Double num;
        while (it.current() != CharacterIterator.DONE) {
            // очищаю токен до пустой строки
            token.delete(0, token.length());
            // если очередной символ является цифрой или точкой,которые вместе формируют число
            if (isDigit(it.current())) {
                token.append(it.current());
                it.next();
                while (isDigit(it.current()) && (it.current() != CharacterIterator.DONE)) {
                    token.append(it.current());
                    it.next();
                }
                num = Double.parseDouble(String.valueOf(token));
                numbers.push(num);
            }
            // если очередной символ является математической операцией
            else if (isOperator(it.current())) {
                // обработка ситуации когда выражение начинается с символа "минус", т.е. операнд - отрицательное число
                if (numbers.size() <= (operators.size() - bracketsCount)) {

                    numbers.push(-1.0);
                    operators.push('*');
                } else {
                    // обработка ситуации,когда операнд положительное число
                    while (!operators.empty() && precedence(operators.peek())
                            >= precedence(it.current())) {
                        Double b = numbers.pop();
                        Double a = numbers.pop();
                        Character op = operators.pop();
                        numbers.push(applyOp(a, b, op));
                    }
                    operators.push(it.current());
                }
                it.next();
            }

            // если открывающаяся скобка
            else if (it.current() == '(') {
                bracketsCount++;
                operators.push(it.current());
                it.next();
            }
            // если закрывающая скобка
            else if (it.current() == ')') {
                bracketsCount--;
                while (!operators.empty() && operators.peek() != '(') {
                    // вычисляю значение внутри скобок
                    double d = numbers.pop();
                    double c = numbers.pop();
                    char op = operators.pop();
                    // Результата вычисления внутри скобок помещяю в стэк операндов (значений)
                    numbers.push(applyOp(c, d, op));
                }
                // из стека оператор исключаю открывавшую скобку,того выражения которое только что посчитали
                operators.pop();
                it.next();
            }
        }

        // вычисляю пока не закончаться все операторы (арифметические операции) в стэке
        while (!operators.empty()) {
            // вытягиваю из стэка операндов два последних значения
            double b = numbers.pop();
            double a = numbers.pop();
            // вытягиваю из стэка  операторов последнюю арифметическую операцию
            char op = operators.pop();
            // вычисление промежуточного результата и сохранение его в стэке операндов (значений)
            numbers.push(applyOp(a, b, op));
        }

        // Приведение конечного результат к строке
        String result = Double.toString(numbers.pop());
        return result;
    }

    //метод проверки является ли символ составной частью (цифра или точкой) в разряде числа
    public static boolean isDigit(Character ch) {
        return ((ch >= '0') && (ch <= '9')) || ch == '.';
    }

    // метод проверки является ли символ одной из допустимых арифметических операций
    public static boolean isOperator(Character ch) {
        // возвращает истину если символ является одной из допустимых операций
        return ch.equals('+') || ch.equals('-') || ch.equals('*') || ch.equals('/');
    }

    // метод определяющий приорететность выполняемых математических операций
    public static int precedence(Character ch) {
        // возврщаю приоритет арифметической операции
        if (ch == '+' || ch == '-')
            return 1;
        if (ch == '*' || ch == '/')
            return 2;
        return 0;
    }

    // метод для применения математической операции к двум операндам (значениям)
    public static double applyOp(double a, double b, char op) {
        // Вычисляю значение на основае двух операндов и одного оператора
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                return a / b;
            default:
                return 0;
        }
    }

}
