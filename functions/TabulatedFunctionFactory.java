package functions;

// Интерфейс фабрики для создания табулированных функций
// Паттерн "Фабричный метод" позволяет абстрагировать процесс создания объектов
public interface TabulatedFunctionFactory {
    
    // Создает табулированную функцию по границам и количеству точек
    // Соответствует конструктору: TabulatedFunction(double leftX, double rightX, int pointsCount)
    TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount);
    
    // Создает табулированную функцию по границам и массиву значений
    // Соответствует конструктору: TabulatedFunction(double leftX, double rightX, double[] values)
    TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values);
    
    // Создает табулированную функцию по массиву точек
    // Соответствует конструктору: TabulatedFunction(FunctionPoint[] points)
    TabulatedFunction createTabulatedFunction(FunctionPoint[] points);
}
