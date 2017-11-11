package am.fats;

import java.io.IOException;
import java.util.ArrayDeque;

public class SVGPath_Quadratic_Curve_Abs extends SVGPath_Command
{
    protected FileLineWriter mGCode;
    protected TransformationStack mTrans;

    public SVGPath_Quadratic_Curve_Abs()
    {}

    @Override
    public void process(ArrayDeque<String> commandQueue, FileLineWriter gcode, TransformationStack trans) throws IOException
    {
        //We need to reset mIsOk
        mIsOk = true;

        mGCode = gcode;
        mTrans = trans.clone();
        Point2D startPoint = new Point2D();
        Point2D endPoint = new Point2D();
        Point2D controlPoint = new Point2D();

        comment("Starting quadratic curve", gcode);
        //Head down if necessary
        headDown(gcode);

        //Start point is the current head positon
        startPoint = PlotterState.getPosition();

        while(mIsOk)
        {
            controlPoint.x = readDouble(commandQueue);
            controlPoint.y = readDouble(commandQueue);
            endPoint.x = readDouble(commandQueue);
            endPoint.y = readDouble(commandQueue);

            if(mIsOk)
            {
                double stepSize = 0.01; //Lets see how that goes.  This will mean that there will always be 100 steps per
                //curve.  Lets make it more sophisticated once its working
                //TODO: Calculate an appropriate resolution for each curve

                plotCurve(stepSize, startPoint, endPoint, controlPoint);

                //Make the next start point this endpoint
                startPoint = endPoint;
            }
        }
    }

    protected void plotCurve(double stepSize, Point2D startPoint, Point2D endPoint, Point2D controlPoint) throws IOException
    {
        double stepCounter = 0;
        //I'm not even going to pretend I can interpret this.
        // This terrible code is lifted from https://github.com/avwuff/SVG-to-GCode/blob/master/src/Bezier.bas

        double t1; //What?
        double A;
        double b;
        double c; //Why is this one lower case?

        while(stepCounter < 1)
        {
            t1 = 1 - stepCounter;
            A = t1 * t1;
            b = 2 * stepCounter * t1;
            c = stepCounter * stepCounter;

            Point2D newPoint = new Point2D();
            //Some ill-defined magic
            newPoint.x = A * startPoint.x + b * controlPoint.x + c * endPoint.x;
            newPoint.y = A * startPoint.y + b * controlPoint.y + c * endPoint.y;

            GCodeLine line = new GCodeLine(newPoint.x, newPoint.y);
            line.setTransformationStack(mTrans);
            mGCode.writeLine(line.toString());

            stepCounter += stepSize;
        }

        //Make sure we hit the target.  The final point will be our destination
        GCodeLine line = new GCodeLine(endPoint.x, endPoint.y);
        line.setTransformationStack(mTrans);
        mGCode.writeLine(line.toString());

        //We might be connecting this curve to a subsequent curve, which might assume that the second control point
        //can be used as the first control point.  Save the endControlPoint to the state so we can use it later
        PlotterState.setControlPosition(controlPoint);
    }

}
