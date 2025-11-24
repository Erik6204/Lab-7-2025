package functions;

import java.io.Serializable;

public class FunctionPoint implements Serializable {
    private static final long serialVersionUID = 1L;
    private double x;
    private double y;
    
    public FunctionPoint(double x, double y){
        this.x = x;
        this.y = y;
    }
    
    public FunctionPoint(FunctionPoint point){
        this.x = point.x;
        this.y = point.y;
    } 
    
    public FunctionPoint(){
        this.x = 0;
        this.y = 0;
    }
    
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
    
    // 1 (переопределение методов object)
    
    @Override
    public String toString() {
        return "(" + x + "; " + y + ")";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        FunctionPoint that = (FunctionPoint) o;
        return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0;//для дабл чисел compare
    }
    
    @Override
    public int hashCode() {
        long xBits = Double.doubleToLongBits(x);//преобразует double в 64-битное целое
        long yBits = Double.doubleToLongBits(y);
        
        int xHash = (int)(xBits ^ (xBits >>> 32));//делаем XOR для исходного и сдвинутого значений
        int yHash = (int)(yBits ^ (yBits >>> 32));
        
        return xHash ^ yHash;
    }
    //4
    @Override
    public Object clone() {
        return new FunctionPoint(this);
    }
}
