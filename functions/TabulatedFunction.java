package functions;
import java.util.Iterator;

public interface TabulatedFunction extends Function, Iterable<FunctionPoint>, Cloneable {
    
    // Методы работы с точками
    int getPointsCount(); // Метод, возвращающий количество точек
    FunctionPoint getPoint(int index); // Метод, возвращающий ссылку на объект по индексу
    void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException; // Метод, заменяющий точку по индексу на заданную
    double getPointX(int index); // Возвращает X точки с указанным индексом
    void setPointX(int index, double x) throws InappropriateFunctionPointException; // Метод, изменяющий абсциссу точки по индексу
    double getPointY(int index); // Возвращает Y точки с указанным индексом
    void setPointY(int index, double y); // Метод, изменяющий ординату точки по индексу
    
    // Методы изменения количества точек
    void deletePoint(int index); // Удаление точки по индексу
    void addPoint(FunctionPoint point) throws InappropriateFunctionPointException; // Добавление точки
    
    // Метод вывода
    void printTabulatedFunction(); // вывод в консоль
    
    Object clone();
}
