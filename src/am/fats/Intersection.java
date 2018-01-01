package am.fats;

public class Intersection
{
    protected Point2D mPoint;
    protected boolean mValid;
    protected boolean mParallel;

    public Intersection()
    {
        mPoint = null;
        mValid = true;
        mParallel = false;
    }

    public Intersection(Point2D value)
    {
        mPoint = value;
        mValid = true;
        mParallel = false;
    }

    public void setValid(boolean valid)
    {
        mValid = valid;
    }

    public void setParallel(boolean parallel)
    {
        mParallel = parallel;
    }

    public boolean isIntersection()
    {
        return mValid;
    }

    public boolean isParallel()
    {
        return mParallel;
    }

    public Point2D getPoint()
    {
        if(mPoint == null)
            mPoint = new Point2D();
        return mPoint;
    }
}
