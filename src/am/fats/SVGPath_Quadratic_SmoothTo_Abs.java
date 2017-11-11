package am.fats;

import java.io.IOException;
import java.util.ArrayDeque;

public class SVGPath_Quadratic_SmoothTo_Abs extends SVGPath_Quadratic_Curve_Abs
{
    public SVGPath_Quadratic_SmoothTo_Abs()
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

        comment("Starting quadratic smoothto", gcode);
        //Head down if necessary
        headDown(gcode);

        //Start point is the current head positon
        startPoint = PlotterState.getPosition();
        controlPoint = PlotterState.getControlPoint();

        while(mIsOk)
        {
            endPoint.x = readDouble(commandQueue);
            endPoint.y = readDouble(commandQueue);

            if(mIsOk)
            {
                //I'm assuming that a 0,0 startControlPoint means that it hasn't been set
                //TODO: Well, this might actually be the control position!
                if(controlPoint.x == 0 && controlPoint.y == 0)
                {
                    controlPoint = startPoint;
                }
                {
                    //We have to make the new control point the reflection of the original control point
                    controlPoint.x = ((startPoint.x - controlPoint.x)) + startPoint.x;
                    controlPoint.y = ((startPoint.y - controlPoint.y)) + startPoint.y;
                }

                double stepSize = 0.01; //Lets see how that goes.  This will mean that there will always be 100 steps per
                //curve.  Lets make it more sophisticated once its working
                //TODO: Calculate an appropriate resolution for each curve

                plotCurve(stepSize, startPoint, endPoint, controlPoint);

                //Make the next start point this endpoint
                startPoint = endPoint;
            }
        }
    }

}
