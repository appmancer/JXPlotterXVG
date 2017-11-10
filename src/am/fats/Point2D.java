package am.fats;

public class Point2D {
    public double x;
    public double y;

    public Point2D()
    {
        this.x = 0;
        this.y = 0;
    }

    public Point2D(double nx, double ny)
    {
        this.x = nx;
        this.y = ny;
    }

    public Point2D(Point2D copy)
    {
        this.x = copy.x;
        this.y = copy.y;
    }
}
