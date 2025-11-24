package functions;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.io.Serializable;

public class LinkedListTabulatedFunction implements TabulatedFunction, Serializable {
    private static final long serialVersionUID = 1L;

    private static class FunctionNode {
        private FunctionPoint point;
        private FunctionNode prev, next;
        
        private FunctionNode(FunctionPoint point) {
            this.point = point;
        }
    }

    private final FunctionNode head;
    private int size;

    // Конструктор по умолчанию
    public LinkedListTabulatedFunction() {
        head = new FunctionNode(null);
        head.prev = head;
        head.next = head;
        size = 0;
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        this();
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница области определения должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек табулирования не может быть меньше 2");
        }
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            FunctionNode node = addNodeToTail();
            node.point = new FunctionPoint(leftX, 0);
            leftX += step;
        }
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        this();
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница области определения должна быть меньше правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек табулирования не может быть меньше 2");
        }
        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            FunctionNode node = addNodeToTail();
            node.point = new FunctionPoint(leftX, values[i]);
            leftX += step;
        }
    }
    
    public LinkedListTabulatedFunction(FunctionPoint[] points) {
        this();
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        
        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() <= points[i-1].getX() + 1e-10) {
                throw new IllegalArgumentException("Точки не упорядочены по возрастанию x");
            }
        }
        
        for (FunctionPoint point : points) {
            addNodeToTail().point = new FunctionPoint(point); // ← size увеличивается в addNodeToTail()
        }
    }
    @Override//№1
    public Iterator<FunctionPoint> iterator() {
        return new Iterator<FunctionPoint>() {
            private FunctionNode currentNode = head.next; // Начинаем с первого элемента
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentNode != head && currentIndex < size;
            }

            @Override
            public FunctionPoint next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("Нет следующего элемента");
                }
                FunctionPoint point = new FunctionPoint(currentNode.point); // Копируем точку
                currentNode = currentNode.next; // Переходим к следующему узлу
                currentIndex++;
                return point;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Удаление не поддерживается");
            }
        };
    }
    // Вложенный публичный класс фабрики для LinkedListTabulatedFunction
    public static class LinkedListTabulatedFunctionFactory implements TabulatedFunctionFactory {//№2
        
        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
            return new LinkedListTabulatedFunction(leftX, rightX, pointsCount);
        }
        
        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
            return new LinkedListTabulatedFunction(leftX, rightX, values);
        }
        
        @Override
        public TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
            return new LinkedListTabulatedFunction(points);
        }
    }
    // Левая граница области определения функции
    public double getLeftDomainBorder() {
        if (size == 0) throw new IllegalStateException("Функция не содержит точек");
        return head.next.point.getX();
    }
    
    // Правая граница области определения функции
    public double getRightDomainBorder() {
        if (size == 0) throw new IllegalStateException("Функция не содержит точек");
        return head.prev.point.getX();
    }

    // Метод, возвращающий количество точек
    public int getPointsCount() {
        return size;
    }

    // Метод, возвращающий ссылку на объект по индексу
    public FunctionPoint getPoint(int index) {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        return new FunctionPoint(getNodeByIndex(index).point);
    }

    // Метод, заменяющий точку на заданную
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        FunctionNode node = getNodeByIndex(index);

        double x = point.getX();
        if ((index > 0 && x <= node.prev.point.getX() + 1e-10) || (index < size - 1 && x >= node.next.point.getX() - 1e-10)) {
            throw new InappropriateFunctionPointException("X координата точки нарушает упорядоченность");
        }

        node.point = new FunctionPoint(point);
    }

    // Возвращает X точки с указанным индексом
    public double getPointX(int index) {
        return getPoint(index).getX();
    }

    // Метод, изменяющий значение абсциссы точки с указанным номером
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        FunctionNode node = getNodeByIndex(index);
        if ((index > 0 && x <= node.prev.point.getX() + 1e-10) || (index < size - 1 && x >= node.next.point.getX() - 1e-10)) {
            throw new InappropriateFunctionPointException("X координата точки нарушает упорядоченность");
        }
        node.point.setX(x);
    }

    // Возвращает Y точки с указанным индексом
    public double getPointY(int index) {
        return getPoint(index).getY();
    }

    // Метод, изменяющий значение ординаты точки с указанным номером
    public void setPointY(int index, double y) {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        getNodeByIndex(index).point.setY(y);
    }

    // Добавление точки
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        double x = point.getX();

        if (size == 0) {
            addNodeToTail().point = new FunctionPoint(point);
            size++;
            return;
        }

        // Проверка на существующую точку с таким X
        FunctionNode current = head.next;
        while (current != head) {
            if (Math.abs(current.point.getX() - x) < 1e-10) {
                throw new InappropriateFunctionPointException("Точка с X=" + point.getX() + " уже существует");
            }
            current = current.next;
        }

        if (x < getLeftDomainBorder()) {
            FunctionNode node = addNodeByIndex(0);
            node.point = new FunctionPoint(point);
            return;
        }

        if (x > getRightDomainBorder()) {
            addNodeToTail().point = new FunctionPoint(point);
            return;
        }

        // Иначе вставляем в нужное место
        FunctionNode curr = head.next;
        int index = 0;
        while (curr != head && curr.point.getX() < x) {
            curr = curr.next;
            index++;
        }

        if (curr != head && Math.abs(curr.point.getX() - x) < 1e-10) {
            throw new InappropriateFunctionPointException("Точка с X=" + point.getX() + " уже существует");
        }
        FunctionNode node = addNodeByIndex(index);
        node.point = new FunctionPoint(point);
    }

    // Удаление точки
    public void deletePoint(int index) {
        if (size < 3) throw new IllegalStateException("Невозможно удалить точку: количество точек должно быть не менее 3");
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        deleteNodeByIndex(index);
    }

    // Метод, возвращающий значение функции в точке х
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }
        FunctionNode current = head.next;
        while (current != head && current.next != head) {
            double x1 = current.point.getX();
            double y1 = current.point.getY();
            double x2 = current.next.point.getX();
            double y2 = current.next.point.getY();

            if (x >= x1 && x <= x2) {
                return y1 + (x - x1) * (y2 - y1) / (x2 - x1);
            }
            current = current.next;
        }
        return Double.NaN;
    }

    // Возвращает ссылку на объект элемента списка по его индексу
    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        FunctionNode current;

        if (index < size / 2) {
            current = head.next;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = head.prev;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
        }
        return current;
    }
    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode(null);
        FunctionNode last = head.prev;
        last.next = newNode;
        newNode.prev = last;
        newNode.next = head;
        head.prev = newNode;

        size++;
        return newNode;
    }
    private FunctionNode addNodeByIndex(int index) {
        if (index == size) return addNodeToTail();
        FunctionNode nextNode = getNodeByIndex(index);
        FunctionNode newNode = new FunctionNode(null);

        newNode.next = nextNode;
        newNode.prev = nextNode.prev;
        nextNode.prev.next = newNode;
        nextNode.prev = newNode;

        size++;
        return newNode;
    }
    private FunctionNode deleteNodeByIndex(int index) {
        FunctionNode node = getNodeByIndex(index);
        node.prev.next = node.next;
        node.next.prev = node.prev;
        size--;
        return node;
    }
    public void printTabulatedFunction() {
        for (int i = 0; i < size; i++) {
            System.out.println("x = " + getPointX(i) + ", y = " + getPointY(i));
        }
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        FunctionNode current = head.next;
        while (current != head) {
            sb.append(current.point.toString());
            if (current.next != head) {
                sb.append(", ");
            }
            current = current.next;
        }
        sb.append("}");
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TabulatedFunction)) return false;
        
        TabulatedFunction that = (TabulatedFunction) o;
        
        if (this.getPointsCount() != that.getPointsCount()) return false;
        
        if (o instanceof LinkedListTabulatedFunction) {
            LinkedListTabulatedFunction listThat = (LinkedListTabulatedFunction) o;
            FunctionNode currentThis = this.head.next;
            FunctionNode currentThat = listThat.head.next;
            
            while (currentThis != this.head && currentThat != listThat.head) {
                if (!currentThis.point.equals(currentThat.point)) {
                    return false;
                }
                currentThis = currentThis.next;
                currentThat = currentThat.next;
            }
        } else {
            for (int i = 0; i < getPointsCount(); i++) {
                if (!this.getPoint(i).equals(that.getPoint(i))) {
                    return false;
                }
            }
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = getPointsCount();
        FunctionNode current = head.next;
        while (current != head) {
            hash ^= current.point.hashCode();
            current = current.next;
        }
        return hash;
    }
    
    @Override
    public Object clone() {
        LinkedListTabulatedFunction cloned = new LinkedListTabulatedFunction();
        FunctionNode current = head.next;
        FunctionNode clonedCurrent = cloned.head;
        
        while (current != head) {
            FunctionNode newNode = new FunctionNode((FunctionPoint) current.point.clone());
            clonedCurrent.next = newNode;
            newNode.prev = clonedCurrent;
            clonedCurrent = newNode;
            current = current.next;
        }
        
        clonedCurrent.next = cloned.head;
        cloned.head.prev = clonedCurrent;
        cloned.size = this.size;
        
        return cloned;
    }
}