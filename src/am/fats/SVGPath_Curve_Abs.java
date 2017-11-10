package am.fats;

import java.io.IOException;
import java.util.ArrayDeque;

public class SVGPath_Curve_Abs extends SVGPath_Command
{
    protected FileLineWriter mGcode;
    protected TransformationStack mTrans;

    public SVGPath_Curve_Abs()
    {

    }

    @Override
    public void process(ArrayDeque<String> commandQueue, FileLineWriter gcode, TransformationStack trans) throws IOException
    {
        mGcode = gcode;
        mTrans = trans.clone();
        Point2D startPoint = new Point2D();
        Point2D endPoint = new Point2D();
        Point2D startControlPoint = new Point2D();
        Point2D endControlPoint = new Point2D();

        startPoint = PlotterState.getLogicalPosition();

        comment("Starting curve", gcode);

        //Head down if necessary
        headDown(gcode);

        while(mIsOk)
        {
            startControlPoint.x = readDouble(commandQueue);
            startControlPoint.y = readDouble(commandQueue);
            endControlPoint.x = readDouble(commandQueue);
            endControlPoint.y = readDouble(commandQueue);
            endPoint.x = readDouble(commandQueue);
            endPoint.y = readDouble(commandQueue);

            if(mIsOk)
            {
                double stepSize = 0.1; //Lets see how that goes.  This will mean that there will always be 100 steps per
                //curve.  Lets make it more sophisticated once its working
                //TODO: Calculate an appropriate resolution for each curve

                plotCurve(stepSize, startPoint, endPoint, startControlPoint, endControlPoint);

                //Make the next start point this endpoint
                startPoint = endPoint;
            }
        }
        comment("Curve complete", gcode);
    }

    void plotCurve(double stepSize, Point2D startPoint, Point2D endPoint, Point2D startControlPoint, Point2D endControlPoint) throws IOException {
        double stepCounter = 0;
        Point2D newPoint;

        //From the magic code at https://github.com/mythagel/nc_tools/blob/master/src/nc_svgpath/svgpath.cpp
        for(double t = 0.0; t < 1.0; t += stepSize)
        {
            Point2D ab = lerp(startPoint, startControlPoint, t);
            Point2D bc = lerp(startControlPoint, endControlPoint, t);
            Point2D cd = lerp(endControlPoint, endPoint, t);
            newPoint = lerp(lerp(ab, bc, t), lerp(bc, cd, t), t);

            GCodeLine line = new GCodeLine(newPoint.x, newPoint.y);
            if(mTrans != null)
                line.setTransformationStack(mTrans);

            mGcode.writeLine(line.toString());
        }
        //Make sure we hit the target.  The final point will be our destination
        GCodeLine line = new GCodeLine(endPoint.x, endPoint.y);
        line.setTransformationStack(mTrans);
        mGcode.writeLine(line.toString());

        //We might be connecting this curve to a subsequent curve, which might assume that the second control point
        //can be used as the first control point.  Save the endControlPoint to the state so we can use it later
        PlotterState.setControlPosition(endControlPoint);

        //Roger and out
    }

    protected Point2D lerp(final Point2D p0, final Point2D p1, double t)
    {
        return new Point2D((1-t)*p0.x + t*p1.x, (1-t)*p0.y + t*p1.y);
    }
}
