package am.fats;

import java.io.IOException;
import java.util.ArrayDeque;

public class SVGPath_Vertical_Line_Rel extends SVGPath_Vertical_Line_Abs
{
    public SVGPath_Vertical_Line_Rel()
    {}

    @Override
    public void process(ArrayDeque<String> commandQueue, FileLineWriter gcode, TransformationStack trans) throws IOException
    {
        //We need to reset mIsOk
        mIsOk = true;

        double y;

        comment("line", gcode);
        //Head down if necessary
        headDown(gcode);

        while(mIsOk)
        {
            //Read x value
            y = readDouble(commandQueue);

            if(mIsOk)
            {
                //Get the y value from the current state
                Point2D p = PlotterState.getLogicalPosition();

                //We have an Y double from the command queue
                GCodeLine line = new GCodeLine(p.x, p.y + y);
                line.setTransformationStack(trans);

                gcode.writeLine(line.toString());
            }

            //Loop until we are not OK (end of data or found another command)
        }
    }
}