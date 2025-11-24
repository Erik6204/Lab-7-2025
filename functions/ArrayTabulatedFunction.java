package functions;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.io.*;

public class ArrayTabulatedFunction implements TabulatedFunction, Serializable, Externalizable {
    private static final long serialVersionUID = 1L;
    private FunctionPoint[] points;
    private int pointsCount;

    // Конструктор по умолчанию для Externalizable
    public ArrayTabulatedFunction() {
        points = new FunctionPoint[10];
        pointsCount = 0;
    }

    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        this.pointsCount = pointsCount;
        this.points = new FunctionPoint[pointsCount + 10];
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, 0);
        }
    }

    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        this.pointsCount = values.length;
        this.points = new FunctionPoint[pointsCount + 10];
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, values[i]);
        }
    }
    
    public ArrayTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        
        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() - points[i-1].getX() <= 1e-10) {
                throw new IllegalArgumentException("Точки не упорядочены по возрастанию x");
            }
        }
        
        this.pointsCount = points.length;
        this.points = new FunctionPoint[pointsCount + 10];
        for (int i = 0; i < pointsCount; i++) {
            this.points[i] = new FunctionPoint(points[i]);
        }
    }
    
    // Методы интерфейса Function
    @Override//№1
    public Iterator<FunctionPoint> iterator() {
        // Создаем анонимный класс итератора
        return new Iterator<FunctionPoint>() {
            private int currentIndex = 0;

            // Проверяет, есть ли следующий элемент
            @Override
            public boolean hasNext() {
                return currentIndex < pointsCount;
            }

            // Возвращает следующий элемент
            @Override
            public FunctionPoint next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("Нет следующего элемента");
                }
                // Возвращаем копию точки для защиты инкапсуляции
                return new FunctionPoint(points[currentIndex++]);
            }

            // Удаление не поддерживается - всегда бросаем исключение
            @Override
            public void remove() {
                throw new UnsupportedOperationException("Удаление не поддерживается");
            }
        };
    }
    public static class ArrayTabulatedFunctionFactory implements TabulatedFunctionFactory {//№2
        
        // Создает ArrayTabulatedFunction по границам и количеству точек
        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
            return new ArrayTabulatedFunction(leftX, rightX, pointsCount);
        }
        
        // Создает ArrayTabulatedFunction по границам и массиву значений
        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
            return new ArrayTabulatedFunction(leftX, rightX, values);
        }
        
        // Создает ArrayTabulatedFunction по массиву точек
        @Override
        public TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
            return new ArrayTabulatedFunction(points);
        }
    }
    public double getLeftDomainBorder() {
        return points[0].getX();
    }

    public double getRightDomainBorder() {
        return points[pointsCount - 1].getX();
    }

    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }
        
        for (int i = 0; i < pointsCount - 1; i++) {
            double x1 = points[i].getX();
            double x2 = points[i + 1].getX();

            if (x >= x1 && x <= x2) {
                double y1 = points[i].getY();
                double y2 = points[i + 1].getY();
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }
        return Double.NaN;
    }

    public int getPointsCount(){
        return pointsCount;
    }
    
    public FunctionPoint getPoint(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы [0, " + (pointsCount-1) + "]");
        }
        return new FunctionPoint(points[index]);
    }
    
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException{
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы [0, " + (pointsCount-1) + "]");
        }
        
        if (index > 0 && point.getX() - points[index - 1].getX() <= 1e-10)  {
            throw new InappropriateFunctionPointException("X координата точки нарушает упорядоченность с предыдущей точкой");
        }
        if (index < pointsCount - 1 && point.getX() >= points[index + 1].getX() - 1e-10) {
            throw new InappropriateFunctionPointException("X координата точки нарушает упорядоченность со следующей точкой");
        }

        points[index] = new FunctionPoint(point);
    }
    
    public double getPointX(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы [0, " + (pointsCount-1) + "]");
        }
        return points[index].getX();
    }
    
    public void setPointX(int index, double x) throws InappropriateFunctionPointException{
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы [0, " + (pointsCount-1) + "]");
        }
        
        if (index > 0 && x <= points[index - 1].getX() + 1e-10) {
            throw new InappropriateFunctionPointException("X координата точки нарушает упорядоченность с предыдущей точкой");
        }
        if (index < pointsCount - 1 && x >= points[index + 1].getX() - 1e-10) {
            throw new InappropriateFunctionPointException("X координата точки нарушает упорядоченность со следующей точкой");
        }

        points[index].setX(x);
    }
    
    public double getPointY(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы [0, " + (pointsCount-1) + "]");
        }
        return points[index].getY();
    }
    
    public void setPointY(int index, double y) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы [0, " + (pointsCount-1) + "]");
        }
        points[index].setY(y);
    }
    
    public void deletePoint(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы [0, " + (pointsCount-1) + "]");
        }
        
        if (pointsCount < 3) {
            throw new IllegalStateException("Невозможно удалить точку: количество точек должно быть не менее 3");
        }
        
        for (int i = index; i < pointsCount - 1; i++) {
            points[i] = points[i + 1];
        }
        points[pointsCount - 1] = null;
        pointsCount--;
    }
    
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException{
        for (int i = 0; i < pointsCount; i++) {
            if (Math.abs(point.getX() - points[i].getX()) < 1e-10) {
                throw new InappropriateFunctionPointException("Точка с X=" + point.getX() + " уже существует");
            }
        }
        
        int insertIndex = 0;
        while (insertIndex < pointsCount && point.getX() > points[insertIndex].getX()) {
            insertIndex++;
        }
        
        if (pointsCount >= points.length) {
            FunctionPoint[] newPoints = new FunctionPoint[points.length * 2];
            System.arraycopy(points, 0, newPoints, 0, pointsCount);
            points = newPoints;
        }
        
        for (int i = pointsCount; i > insertIndex; i--) {
            points[i] = points[i - 1];
        }
        
        points[insertIndex] = new FunctionPoint(point);
        pointsCount++;
    }
    
    public void printTabulatedFunction() {
        for (int i = 0; i < pointsCount; i++) {
            System.out.println("x = " + getPointX(i) + ", y = " + getPointY(i));
        }
    }
    
    // 2(переопределение методов)
    //StringBuilder - это класс в Java для эффективного построения строк.
    @Override
    public String toString() {//StringBuilder-массив char[],который изменяется массив в динамике
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < pointsCount; i++) {
            sb.append(points[i].toString());
            if (i < pointsCount - 1) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TabulatedFunction)) return false;//instanceof - Проверяет, является ли объект 'o' экземпляром класса TabulatedFunction
        
        TabulatedFunction that = (TabulatedFunction) o;
        
        if (this.getPointsCount() != that.getPointsCount()) return false;
        
        if (o instanceof ArrayTabulatedFunction) {
            ArrayTabulatedFunction arrayThat = (ArrayTabulatedFunction) o;
            for (int i = 0; i < pointsCount; i++) {
                if (!this.points[i].equals(arrayThat.points[i])) {
                    return false;
                }
            }
        } else {
            for (int i = 0; i < pointsCount; i++) {
                if (!this.getPoint(i).equals(that.getPoint(i))) {
                    return false;
                }
            }
        }
        
        return true;// Если все проверки пройдены - true
    }
    @Override
    public int hashCode() {
        int hash = pointsCount;
        for (int i = 0; i < pointsCount; i++) {
            hash ^= points[i].hashCode();
        }
        return hash;
    }
    
    @Override
    public Object clone() {
        FunctionPoint[] clonedPoints = new FunctionPoint[points.length];
        for (int i = 0; i < pointsCount; i++) {
            clonedPoints[i] = (FunctionPoint) points[i].clone();
        }
        
        ArrayTabulatedFunction cloned = new ArrayTabulatedFunction();
        cloned.points = clonedPoints;
        cloned.pointsCount = this.pointsCount;
        return cloned;
    }
    
    // Методы Externalizable
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(pointsCount);
        for (int i = 0; i < pointsCount; i++) {
            out.writeDouble(points[i].getX());
            out.writeDouble(points[i].getY());
        }
    }
    
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        pointsCount = in.readInt();
        points = new FunctionPoint[pointsCount + 10];
        for (int i = 0; i < pointsCount; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            points[i] = new FunctionPoint(x, y);
        }
    }
}