package functions;

import java.io.*;
import java.util.StringTokenizer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
public final class TabulatedFunctions {
    // Приватное статическое поле для хранения текущей фабрики
    // Инициализируем фабрикой для ArrayTabulatedFunction по умолчанию
    private static TabulatedFunctionFactory factory = 
        new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory();
    
    // Приватный конструктор чтобы запретить создание экземпляров
    private TabulatedFunctions() {
        throw new AssertionError("Нельзя создать экземпляр класса TabulatedFunctions");
    }
    
    // Метод для установки новой фабрики
    // Позволяет динамически менять тип создаваемых объектов
    public static void setTabulatedFunctionFactory(TabulatedFunctionFactory newFactory) {
        factory = newFactory;
    }
    
    // Три перегруженных фабричных метода создания табулированных функций
    // Делегируют создание объектов текущей фабрике
    
    // Создает табулированную функцию по границам и количеству точек
    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
        return factory.createTabulatedFunction(leftX, rightX, pointsCount);
    }
    
    // Создает табулированную функцию по границам и массиву значений
    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
        return factory.createTabulatedFunction(leftX, rightX, values);
    }
    
    // Создает табулированную функцию по массиву точек
    public static TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
        return factory.createTabulatedFunction(points);
    }
    
    // Существующие методы теперь используют фабрику вместо прямого создания объектов
    
    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Границы табулирования выходят за область определения функции");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        // Создаем массив точек
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);
        
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            double y = function.getFunctionValue(x);
            points[i] = new FunctionPoint(x, y);
        }

        // Используем фабрику 
        return createTabulatedFunction(points);
    }
    public static double getFunctionValue(TabulatedFunction function, double x) {
        return function.getFunctionValue(x);
    }

    public static double getLeftDomainBorder(TabulatedFunction function) {
        return function.getLeftDomainBorder();
    }

    public static double getRightDomainBorder(TabulatedFunction function) {
        return function.getRightDomainBorder();
    }

    // Метод вывода в байтовый поток
    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) {
        try (DataOutputStream dos = new DataOutputStream(out)) {
            dos.writeInt(function.getPointsCount());
            for (int i = 0; i < function.getPointsCount(); i++) {
                FunctionPoint point = function.getPoint(i);
                dos.writeDouble(point.getX());
                dos.writeDouble(point.getY());
            }
        } catch (IOException e) {
            // Пробрасываем RuntimeException, так как IOException - проверяемое исключение
            throw new RuntimeException("Ошибка при выводе функции в поток", e);
        }
    }

    public static TabulatedFunction inputTabulatedFunction(InputStream in) {
        try (DataInputStream dis = new DataInputStream(in)) {
            int pointsCount = dis.readInt();
            FunctionPoint[] points = new FunctionPoint[pointsCount];
            
            for (int i = 0; i < pointsCount; i++) {
                double x = dis.readDouble();
                double y = dis.readDouble();
                points[i] = new FunctionPoint(x, y);
            }
            
            // Используем фабрику вместо прямого создания
            return createTabulatedFunction(points);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении функции из потока", e);
        }
    }

    // Метод readTabulatedFunction
    public static TabulatedFunction readTabulatedFunction(Reader in) {
        try {
            StreamTokenizer tokenizer = new StreamTokenizer(in);
            tokenizer.parseNumbers();
            
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new RuntimeException("Ожидалось количество точек");
            }
            int pointsCount = (int) tokenizer.nval;
            
            FunctionPoint[] points = new FunctionPoint[pointsCount];
            
            for (int i = 0; i < pointsCount; i++) {
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new RuntimeException("Ожидалась координата x");
                }
                double x = tokenizer.nval;
                
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new RuntimeException("Ожидалась координата y");
                }
                double y = tokenizer.nval;
                
                points[i] = new FunctionPoint(x, y);
            }
            
            // Используем фабрику вместо прямого создания
            return createTabulatedFunction(points);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении функции из потока", e);
        }
    }
    // Метод записи в символьный поток
    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) {
        try (PrintWriter writer = new PrintWriter(out)) {
            writer.print(function.getPointsCount() + " ");
            for (int i = 0; i < function.getPointsCount(); i++) {
                FunctionPoint point = function.getPoint(i);
                writer.print(point.getX() + " " + point.getY() + " ");
            }
        }
        // PrintWriter не бросает IOException в методах print/println
    }

    

    public static TabulatedFunction createTabulatedFunction(
            Class<?> functionClass, double leftX, double rightX, int pointsCount) {
        
        // Проверяем, что класс реализует TabulatedFunction
        if (!TabulatedFunction.class.isAssignableFrom(functionClass)) {
            throw new IllegalArgumentException(
                "Класс " + functionClass.getName() + " не реализует интерфейс TabulatedFunction");
        }
        
        try {
            // Ищем конструктор с параметрами (double, double, int)
            Constructor<?> constructor = functionClass.getConstructor(
                double.class, double.class, int.class);
            
            // Создаем объект через рефлексию
            return (TabulatedFunction) constructor.newInstance(leftX, rightX, pointsCount);
            
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                "Класс " + functionClass.getName() + " не имеет конструктора (double, double, int)", e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(
                "Ошибка при создании объекта " + functionClass.getName(), e);
        }
    }
    
    public static TabulatedFunction createTabulatedFunction(
            Class<?> functionClass, double leftX, double rightX, double[] values) {
        
        if (!TabulatedFunction.class.isAssignableFrom(functionClass)) {
            throw new IllegalArgumentException(
                "Класс " + functionClass.getName() + " не реализует интерфейс TabulatedFunction");
        }
        
        try {
            // Ищем конструктор с параметрами (double, double, double[])
            Constructor<?> constructor = functionClass.getConstructor(
                double.class, double.class, double[].class);
            
            return (TabulatedFunction) constructor.newInstance(leftX, rightX, values);
            
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                "Класс " + functionClass.getName() + " не имеет конструктора (double, double, double[])", e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(
                "Ошибка при создании объекта " + functionClass.getName(), e);
        }
    }
    

    public static TabulatedFunction createTabulatedFunction(
            Class<?> functionClass, FunctionPoint[] points) {
        
        if (!TabulatedFunction.class.isAssignableFrom(functionClass)) {
            throw new IllegalArgumentException(
                "Класс " + functionClass.getName() + " не реализует интерфейс TabulatedFunction");
        }
        
        try {
            // Ищем конструктор с параметрами (FunctionPoint[])
            Constructor<?> constructor = functionClass.getConstructor(FunctionPoint[].class);
            
            return (TabulatedFunction) constructor.newInstance((Object) points);
            
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                "Класс " + functionClass.getName() + " не имеет конструктора (FunctionPoint[])", e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(
                "Ошибка при создании объекта " + functionClass.getName(), e);
        }
    }

    public static TabulatedFunction tabulate(
            Class<?> functionClass, Function function, double leftX, double rightX, int pointsCount) {
        
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Границы табулирования выходят за область определения функции");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        // Создаем массив точек
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);
        
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            double y = function.getFunctionValue(x);
            points[i] = new FunctionPoint(x, y);
        }

        // Используем рефлексивное создание
        return createTabulatedFunction(functionClass, points);
    }
    public static TabulatedFunction inputTabulatedFunction(
        Class<?> functionClass, InputStream in) {
    
        if (!TabulatedFunction.class.isAssignableFrom(functionClass)) {
            throw new IllegalArgumentException(
                "Класс " + functionClass.getName() + " не реализует интерфейс TabulatedFunction");
        }
        
        try (DataInputStream dis = new DataInputStream(in)) {
            int pointsCount = dis.readInt();
            FunctionPoint[] points = new FunctionPoint[pointsCount];
            
            for (int i = 0; i < pointsCount; i++) {
                double x = dis.readDouble();
                double y = dis.readDouble();
                points[i] = new FunctionPoint(x, y);
            }
            
            // Используем рефлексивное создание
            return createTabulatedFunction(functionClass, points);
            
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении функции из потока", e);
        }
    }

    public static TabulatedFunction readTabulatedFunction(
            Class<?> functionClass, Reader in) {
        
        if (!TabulatedFunction.class.isAssignableFrom(functionClass)) {
            throw new IllegalArgumentException(
                "Класс " + functionClass.getName() + " не реализует интерфейс TabulatedFunction");
        }
        
        try {
            StreamTokenizer tokenizer = new StreamTokenizer(in);
            tokenizer.parseNumbers();
            
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new RuntimeException("Ожидалось количество точек");
            }
            int pointsCount = (int) tokenizer.nval;
            
            FunctionPoint[] points = new FunctionPoint[pointsCount];
            
            for (int i = 0; i < pointsCount; i++) {
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new RuntimeException("Ожидалась координата x");
                }
                double x = tokenizer.nval;
                
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new RuntimeException("Ожидалась координата y");
                }
                double y = tokenizer.nval;
                
                points[i] = new FunctionPoint(x, y);
            }
            
            // Используем рефлексивное создание
            return createTabulatedFunction(functionClass, points);
            
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении функции из потока", e);
        }
    }
}
