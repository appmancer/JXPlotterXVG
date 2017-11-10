package am.fats;

public class Transformation implements Cloneable {
    public double a;
    public double b;
    public double c;
    public double d;
    public double e;
    public double f;

    public Transformation clone() throws CloneNotSupportedException
    {
        return (Transformation)super.clone();
    }
}
