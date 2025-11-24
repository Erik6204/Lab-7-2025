package functions;

import functions.meta.*;

public final class Functions {
    // Приватный конструктор чтобы запретить создание экземпляров
    private Functions() {
        throw new AssertionError("Нельзя создать экземпляр класса Functions");
    }

    /**
     * Вычисляет интеграл функции методом трапеций
     * @param function интегрируемая функция
     * @param leftBorder левая граница интегрирования
     * @param rightBorder правая граница интегрирования  
     * @param discretizationStep шаг дискретизации
     * @return значение интеграла
     * @throws IllegalArgumentException если границы интегрирования выходят за область определения функции
     */
    public static double integrate(Function function, double leftBorder, double rightBorder, double discretizationStep) {
        // Проверка корректности границ интегрирования
        if (leftBorder < function.getLeftDomainBorder()) {
            throw new IllegalArgumentException(
                "Левая граница интегрирования " + leftBorder + 
                " выходит за левую границу области определения функции " + function.getLeftDomainBorder()
            );
        }
        
        if (rightBorder > function.getRightDomainBorder()) {
            throw new IllegalArgumentException(
                "Правая граница интегрирования " + rightBorder + 
                " выходит за правую границу области определения функции " + function.getRightDomainBorder()
            );
        }
        
        if (leftBorder >= rightBorder) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        
        if (discretizationStep <= 0) {
            throw new IllegalArgumentException("Шаг дискретизации должен быть положительным");
        }
        
        double integral = 0.0;
        double currentX = leftBorder;
        
        // Проходим по всем отрезкам интегрирования
        while (currentX < rightBorder) {
            double nextX = Math.min(currentX + discretizationStep, rightBorder);
            double segmentLength = nextX - currentX;
            
            // Вычисляем значения функции на концах отрезка
            double fCurrent = function.getFunctionValue(currentX);
            double fNext = function.getFunctionValue(nextX);
            
            // Если функция возвращает NaN на каком-то отрезке, возвращаем NaN
            if (Double.isNaN(fCurrent) || Double.isNaN(fNext)) {
                return Double.NaN;
            }
            
            // Площадь трапеции для текущего отрезка
            double segmentArea = (fCurrent + fNext) * segmentLength / 2.0;
            integral += segmentArea;
            
            currentX = nextX;
        }
        
        return integral;
    }
}
