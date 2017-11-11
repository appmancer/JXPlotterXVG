package am.fats;

import java.io.IOException;
import java.util.ArrayDeque;

public class SVGPath_Quadratic_Curve_Rel extends SVGPath_Quadratic_Curve_Abs
{
    public SVGPath_Quadratic_Curve_Rel()
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
        startPoint = PlotterState.getLogicalPosition();

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

                TransformationStack inputTrans = new TransformationStack();
                inputTrans.pushTranslate(startPoint.x, startPoint.y);

                //Perform transformations, then calculate curve
                Point2D transEndPoint = inputTrans.process(endPoint.x, endPoint.y);
                Point2D transControlPoint = inputTrans.process(controlPoint.x, controlPoint.y);

                //Call the abs version of plotCurve, but with translated points
                plotCurve(stepSize, startPoint, transEndPoint, transControlPoint);

                //Make the next start point this endpoint
                startPoint = transEndPoint;
            }
        }
    }
}
