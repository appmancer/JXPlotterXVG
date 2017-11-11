package am.fats;

import java.io.IOException;
import java.util.ArrayDeque;

public class SVGPath_Elliptical_Arc_Rel extends SVGPath_Elliptical_Arc_Abs
{
    public SVGPath_Elliptical_Arc_Rel()
    {}


    @Override
    public void process(ArrayDeque<String> commandQueue, FileLineWriter gcode, TransformationStack trans) throws IOException
    {
        //We need to reset mIsOk
        mIsOk = true;

        mGCode = gcode;
        mTrans = trans.clone();
        // SVG Params
        //(rx ry x-axis-rotation large-arc-flag sweep-flag x y)+
        double rx;
        double ry;
        double xAxisRotation;
        double largeArcFlag;
        double sweepFlag;

        //These are endpoint of the ellipse, and come from data
        double x;
        double y;

        comment("Starting elliptical arc", gcode);


        while(mIsOk)
        {
            //Read x value
            rx = readDouble(commandQueue);
            ry = readDouble(commandQueue);
            xAxisRotation = readDouble(commandQueue);
            largeArcFlag = readDouble(commandQueue);
            sweepFlag = readDouble(commandQueue);
            x = readDouble(commandQueue);
            y = readDouble(commandQueue);

            if(mIsOk)
            {
                //Radius should not be negative, but apparently often are
                rx = Math.abs(rx);
                ry = Math.abs(ry);

                //If either radius is 0, then we should just draw a straight line
                if(rx == 0 || ry == 0)
                {
                    GCodeLine line = new GCodeLine(x, y);
                    line.setTransformationStack(trans);
                    gcode.writeLine(line.toString());

                    //and exit
                    mIsOk = false;
                    return;
                }
            }

            if(mIsOk)
            {
                Point2D startPoint = PlotterState.getLogicalPosition();
                drawArc(rx, ry, xAxisRotation, largeArcFlag, sweepFlag, startPoint.x + x, startPoint.y + y);
            }
        }
    }

}
