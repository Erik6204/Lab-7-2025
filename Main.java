import functions.*;
import functions.basic.*;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.StringReader;
public class Main {
    public static void main(String[] args) {
        System.out.println("ЛАБОРАТОРНАЯ РАБОТА №7");
        
        try {
            // Задание 1: Тестирование итераторов
            testIterators();
            
            // Задание 2: Тестирование фабрик
            testFactories();
            
            // Задание 3: Тестирование рефлексии
            testReflection();
            
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
    * Функция для демонстрации 1 задани я
    */
    private static void testIterators() {
        System.out.println("ЗАДАНИЕ 1 (Итераторы)");
        
        // Тест 1: ArrayTabulatedFunction с итератором
        System.out.println("1. ArrayTabulatedFunction с for-each циклом:");
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(0, 10, 6);
        
        // Заполняем значениями y = 2x + 1
        for (int i = 0; i < arrayFunc.getPointsCount(); i++) {
            double x = arrayFunc.getPointX(i);
            arrayFunc.setPointY(i, 2*x + 1);
        }
        
        // ТЕСТ ИТЕРАТОРА: for-each цикл
        System.out.println("Все точки функции через for-each:");
        for (FunctionPoint point : arrayFunc) {
            System.out.println("   " + point);
        }
        
        // Тест 2: LinkedListTabulatedFunction с итератором
        System.out.println("\n2. LinkedListTabulatedFunction с for-each циклом:");
        TabulatedFunction linkedFunc = new LinkedListTabulatedFunction(0, 10, 6);
        
        // Заполняем значениями y = x²
        for (int i = 0; i < linkedFunc.getPointsCount(); i++) {
            double x = linkedFunc.getPointX(i);
            linkedFunc.setPointY(i, x * x);
        }
        
        // ТЕСТ ИТЕРАТОРА: for-each цикл
        System.out.println("Все точки функции через for-each:");
        for (FunctionPoint point : linkedFunc) {
            System.out.println("   " + point);
        }
        
        // Тест 3: Проверка исключений итератора
        System.out.println("\n3. Тестирование исключений итераторов:");
        
        // Тестирование NoSuchElementException
        System.out.println("a) NoSuchElementException:");
        Iterator<FunctionPoint> iterator = arrayFunc.iterator();
        try {
            // Проходим все элементы
            while (iterator.hasNext()) {
                iterator.next();
            }
            // Пытаемся получить следующий, когда элементов нет
            iterator.next();
        } catch (NoSuchElementException e) {
            System.out.println("Поймано: " + e.getMessage());
        }
        
        // Тестирование UnsupportedOperationException
        System.out.println("b) UnsupportedOperationException:");
        iterator = linkedFunc.iterator();
        try {
            iterator.next(); // Получаем первый элемент
            iterator.remove(); // Пытаемся удалить - должно бросить исключение
        } catch (UnsupportedOperationException e) {
            System.out.println("Поймано: " + e.getMessage());
        }
        
        // Тест 4: Сравнение работы итератора с обычным циклом
        System.out.println("\n4. Сравнение итератора с обычным циклом:");
        
        System.out.println("ArrayTabulatedFunction - обычный цикл:");
        for (int i = 0; i < arrayFunc.getPointsCount(); i++) {
            FunctionPoint point = arrayFunc.getPoint(i);
            System.out.println("   " + point);
        }
        
        System.out.println("ArrayTabulatedFunction - for-each цикл:");
        for (FunctionPoint point : arrayFunc) {
            System.out.println("   " + point);
        }
        
        // Тест 5: Проверка инкапсуляции
        System.out.println("\n5. Проверка защиты инкапсуляции:");
        System.out.println("Получаем точку через итератор и пытаемся изменить:");
        iterator = arrayFunc.iterator();
        if (iterator.hasNext()) {
            FunctionPoint pointFromIterator = iterator.next();
            double originalY = pointFromIterator.getY();
            pointFromIterator.setY(999); // Пытаемся изменить копию
            
            // Проверяем, изменилась ли оригинальная точка
            FunctionPoint originalPoint = arrayFunc.getPoint(0);
            if (originalPoint.getY() != 999) {
                System.out.println("Инкапсуляция защищена: изменение копии не затронуло оригинал");
                System.out.println("Оригинал: " + originalPoint.getY() + ", Копия: " + pointFromIterator.getY());
            }
        }

    }
    
    /**
     * Функция для демонстрации 2 задания 
    */
    private static void testFactories() {
        System.out.println("ЗАДАНИЕ 2(Фабрики)");
        
        // Создаем тестовую функцию для табулирования
        Function f = new Cos();
        
        TabulatedFunction tf;
        
        // Тест 1: Фабрика по умолчанию (должна создавать ArrayTabulatedFunction)
        System.out.println("1. Фабрика по умолчанию:");
        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println("Тип созданного объекта: " + tf.getClass().getSimpleName());
        
        // Тест 2: Устанавливаем фабрику для LinkedListTabulatedFunction
        System.out.println("\n2. Устанавливаем LinkedListTabulatedFunctionFactory:");
        TabulatedFunctions.setTabulatedFunctionFactory(
            new LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println("Тип созданного объекта: " + tf.getClass().getSimpleName());
        
        // Тест 3: Прямое использование фабричных методов ДОЛЖНО создавать LinkedList
        System.out.println("\n3. Прямое использование фабричных методов (должно создавать LinkedList):");
        
        // Эти методы должны использовать текущую фабрику (LinkedList)
        double[] values = {0, 1, 4, 9, 16};
        tf = TabulatedFunctions.createTabulatedFunction(0, 4, values);
        System.out.println("Создана функция с массивом значений: " + tf.getClass().getSimpleName());
        
        FunctionPoint[] points = {
            new FunctionPoint(0, 0),
            new FunctionPoint(1, 1),
            new FunctionPoint(2, 8)
        };
        tf = TabulatedFunctions.createTabulatedFunction(points);
        System.out.println("Создана функция с массивом точек: " + tf.getClass().getSimpleName());
        
        // Тест 4: Возвращаем фабрику для ArrayTabulatedFunction
        System.out.println("\n4. Возвращаем ArrayTabulatedFunctionFactory:");
        TabulatedFunctions.setTabulatedFunctionFactory(
            new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println("Тип созданного объекта: " + tf.getClass().getSimpleName());
        
        // Тест 5: Прямое использование фабричных методов
        System.out.println("\n5. Прямое использование фабричных методов:");
        
        // Создаем функцию через фабричный метод с массивом значений
        double[] values2 = {0, 1, 4, 9, 16};
        tf = TabulatedFunctions.createTabulatedFunction(0, 4, values2);
        System.out.println("Создана функция с массивом значений: " + tf.getClass().getSimpleName());
        
        // Создаем функцию через фабричный метод с массивом точек
        FunctionPoint[] points2 = {
            new FunctionPoint(0, 0),
            new FunctionPoint(1, 1),
            new FunctionPoint(2, 8)
        };
        tf = TabulatedFunctions.createTabulatedFunction(points2);
        System.out.println("Создана функция с массивом точек: " + tf.getClass().getSimpleName());
        
        // Тест 6: Демонстрация полиморфизма
        System.out.println("\n6. Демонстрация полиморфизма:");
        System.out.println("Все созданные функции поддерживают for-each:");
        
        // Несмотря на разный тип, все функции работают с for-each
        for (FunctionPoint point : tf) {
            System.out.println("   " + point);
        }

    }
    /**
     * Функция для демонстрации 3 задания 
    */
    private static void testReflection() {
        System.out.println("ЗАДАНИЕ 3 (Рефлексии)");
        
        TabulatedFunction tf = null; // Инициализируем null
        
        // Тест 1: Создание ArrayTabulatedFunction через рефлексию
        System.out.println("1. Создание ArrayTabulatedFunction через рефлексию:");
        try {
            tf = TabulatedFunctions.createTabulatedFunction(
                ArrayTabulatedFunction.class, 0, 10, 3);
            System.out.println("Тип созданного объекта: " + tf.getClass().getSimpleName());
            System.out.println("Функция: " + tf);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        
        // Тест 2: Создание ArrayTabulatedFunction с массивом значений
        System.out.println("\n2. Создание ArrayTabulatedFunction с массивом значений:");
        try {
            double[] values = {0, 1, 4, 9, 16};
            tf = TabulatedFunctions.createTabulatedFunction(
                ArrayTabulatedFunction.class, 0, 4, values);
            System.out.println("Тип созданного объекта: " + tf.getClass().getSimpleName());
            System.out.println("Функция: " + tf);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        
        // Тест 3: Создание LinkedListTabulatedFunction через рефлексию
        System.out.println("\n3. Создание LinkedListTabulatedFunction через рефлексию:");
        try {
            FunctionPoint[] points = {
                new FunctionPoint(0, 0),
                new FunctionPoint(1, 1),
                new FunctionPoint(2, 8)
            };
            tf = TabulatedFunctions.createTabulatedFunction(
                LinkedListTabulatedFunction.class, points);
            System.out.println("Тип созданного объекта: " + tf.getClass().getSimpleName());
            System.out.println("Функция: " + tf);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        
        // Тест 4: Табулирование с использованием рефлексии
        System.out.println("\n4. Табулирование функции Sin с использованием рефлексии:");
        try {
            Function sinFunction = new Sin();
            tf = TabulatedFunctions.tabulate(
                LinkedListTabulatedFunction.class, sinFunction, 0, Math.PI, 11);
            System.out.println("Тип созданного объекта: " + tf.getClass().getSimpleName());
            System.out.println("Первые 5 точек функции Sin:");
            int count = 0;
            for (FunctionPoint point : tf) {
                if (count++ >= 5) break;
                System.out.println("   " + point);
            }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        
        // Тест 5: Обработка ошибок рефлексии
        System.out.println("\n5. Тестирование обработки ошибок:");

        // Попытка создать объект класса, не реализующего TabulatedFunction
        System.out.println("a) Передача неправильного класса:");
        try {
            TabulatedFunction tempTf = TabulatedFunctions.createTabulatedFunction(
                String.class, 0, 10, 3); // String не реализует TabulatedFunction
        } catch (IllegalArgumentException e) {
            System.out.println("Поймано исключение: " + e.getMessage());
        }

        System.out.println("b) Передача несуществующего класса:");
        try {
            // Создаем фиктивный класс, который не существует
            Class<?> nonExistentClass = Class.forName("functions.NonExistentClass");
            TabulatedFunction tempTf = TabulatedFunctions.createTabulatedFunction(
                nonExistentClass, 0, 10, 3);
        } catch (ClassNotFoundException e) {
            System.out.println("Поймано исключение: Класс не найден");
        } catch (IllegalArgumentException e) {
            System.out.println("Поймано исключение: " + e.getMessage());
        }
        
        // Тест 6: Чтение из потоков с использованием рефлексии
        System.out.println("\n6. Чтение из потоков с использованием рефлексии:");

        // Проверяем, что tf была инициализирована в предыдущих тестах
        if (tf == null) {
            System.out.println("Пропуск теста: tf не инициализирована");
            return;
        }

        // Тест 6a: Чтение из байтового потока
        System.out.println("a) Чтение из байтового потока:");
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            TabulatedFunctions.outputTabulatedFunction(tf, byteOut);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            
            // Читаем с рефлексией
            TabulatedFunction tfFromByteStream = TabulatedFunctions.inputTabulatedFunction(
                LinkedListTabulatedFunction.class, byteIn);
            System.out.println("Тип созданного объекта: " + tfFromByteStream.getClass().getSimpleName());
            System.out.println("Функция: " + tfFromByteStream);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        // Тест 6b: Чтение из символьного потока
        System.out.println("b) Чтение из символьного потока:");
        try {
            StringWriter stringWriter = new StringWriter();
            TabulatedFunctions.writeTabulatedFunction(tf, stringWriter);
            StringReader stringReader = new StringReader(stringWriter.toString());
            
            // Читаем с рефлексией
            TabulatedFunction tfFromCharStream = TabulatedFunctions.readTabulatedFunction(
                ArrayTabulatedFunction.class, stringReader);
            System.out.println("Тип созданного объекта: " + tfFromCharStream.getClass().getSimpleName());
            System.out.println("Функция: " + tfFromCharStream);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

    }
    
}