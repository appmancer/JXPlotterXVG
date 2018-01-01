package am.fats;


import am.fats.Point2D;
import am.fats.Intersection;

public class Line {
    protected Point2D mStart;
    protected Point2D mEnd;

    public Line(Point2D start, Point2D end)
    {
        mStart = start;
        mEnd = end;
    }

    public Line(double x1, double y1, double x2, double y2)
    {
        mStart = new Point2D(x1, y1);
        mEnd = new Point2D(x2, y2);
    }

    public Point2D getStart()
    {
        return mStart;
    }

    public Point2D getEnd()
    {
        return mEnd;
    }

    public Intersection intersects(Line other) {
        Intersection i = new Intersection();
        double x1 = this.getStart().x;
        double y1 = this.getStart().y;
        double x2 = this.getEnd().x;
        double y2 = this.getEnd().y;
        double x3 = other.getStart().x;
        double y3 = other.getStart().y;
        double x4 = other.getEnd().x;
        double y4 = other.getEnd().y;

        double minx = x1;
        if (x2 < minx) minx = x2;
        if (x3 < minx) minx = x3;
        if (x4 < minx) minx = x4;

        double maxx = y1;
        if (x2 > maxx) maxx = x2;
        if (x3 > maxx) maxx = x3;
        if (x4 > maxx) maxx = x4;

        double miny = y1;
        if (y2 < miny) miny = y2;
        if (y3 < miny) miny = y3;
        if (y4 < miny) miny = y4;

        double maxy = y1;
        if (y2 > maxy) maxy = y2;
        if (y3 > maxy) maxy = y3;
        if (y4 > maxy) maxy = y4;


        //Get the X intersection
        //https://en.wikipedia.org/wiki/Line%E2%80%93line_intersection
        double numerator = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)); // You don't have to understand it, just type the letters in ;-)
        double denominator = ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));

        //if denominator is 0 then the lines are parallel
        if (denominator == 0)
            i.setParallel(true);

        double xPos = numerator / denominator;

        numerator = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4));
        denominator = ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));

        if (denominator == 0)
            i.setParallel(true);

        double yPos = numerator / denominator;
        if (xPos < minx || xPos > maxx || yPos < miny || yPos > maxy) {
            i.setValid(false);
        }

        return i;
    }
}
