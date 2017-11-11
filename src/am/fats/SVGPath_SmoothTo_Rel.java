package am.fats;

import java.io.IOException;
import java.util.ArrayDeque;

public class SVGPath_SmoothTo_Rel extends SVGPath_SmoothTo_Abs
{
    public SVGPath_SmoothTo_Rel()
    {}


    @Override
    public void process(ArrayDeque<String> commandQueue, FileLineWriter gcode, TransformationStack trans) throws IOException
    {
        //We need to reset mIsOk
        mIsOk = true;

        mGcode = gcode;
        mTrans = trans.clone();
        Point2D startPoint = new Point2D();
        Point2D endPoint = new Point2D();
        Point2D startControlPoint = new Point2D();
        Point2D endControlPoint = new Point2D();

        comment("Starting smoothto", gcode);
        //Head down if necessary
        headDown(gcode);

        //Start point is the current head positon
        startPoint = PlotterState.getPosition();
        startControlPoint = PlotterState.getControlPoint();

        while(mIsOk)
        {
            endControlPoint.x = readDouble(commandQueue);
            endControlPoint.y = readDouble(commandQueue);
            endPoint.x = readDouble(commandQueue);
            endPoint.y = readDouble(commandQueue);

            if(mIsOk)
            {
                //I'm assuming that a 0,0 startControlPoint means that it hasn't been set
                //TODO: Well, this might actually be the control position!
                if(startControlPoint.x == 0 && startControlPoint.y == 0)
                {
                    startControlPoint = startPoint;
                }
                {
                    //We have to make the new control point the reflection of the original control point
                    startControlPoint.x = (-(startControlPoint.x - startPoint.x)) + startPoint.x;
                    startControlPoint.y = (-(startControlPoint.y - startPoint.y)) + startPoint.y;
                }

                double stepSize = 0.01; //Lets see how that goes.  This will mean that there will always be 100 steps per
                //curve.  Lets make it more sophisticated once its working
                //TODO: Calculate an appropriate resolution for each curve

                TransformationStack inputTrans = new TransformationStack();
                inputTrans.pushTranslate(startPoint.x, startPoint.y);

                //Perform transformations, then calculate curve
                Point2D transEndPoint = inputTrans.process(endPoint.x, endPoint.y);
                Point2D transStartControlPoint = inputTrans.process(startControlPoint.x, startControlPoint.y);
                Point2D transEndControlPoint = inputTrans.process(endControlPoint.x, endControlPoint.y);

                //We've inhertited plotCurve from SVGPath_Curve_Abs
                plotCurve(stepSize, startPoint, transEndPoint, transStartControlPoint, transEndControlPoint);

                //Make the next start point this endpoint
                startPoint = transEndPoint;
                startControlPoint = transEndControlPoint;
                //And save it for a following curve command
                PlotterState.setControlPosition(transEndControlPoint);
            }
        }
    }
}
