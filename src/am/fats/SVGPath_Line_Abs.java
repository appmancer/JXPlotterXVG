package am.fats;

import java.io.IOException;
import java.util.ArrayDeque;

public class SVGPath_Line_Abs extends SVGPath_Command
{
    public SVGPath_Line_Abs()
    {}

    @Override
    public void process(ArrayDeque<String> commandQueue, FileLineWriter gcode, TransformationStack trans) throws IOException
    {
        //We need to reset mIsOk
        mIsOk = true;

        double x;
        double y;

        comment("line", gcode);
        //Head down if necessary
        headDown(gcode);

        while(mIsOk)
        {
            //Read x value
            x = readDouble(commandQueue);
            y = readDouble(commandQueue);

            if(mIsOk)
            {
                //We have an X double from the command queue
                GCodeLine line = new GCodeLine(x, y);
                line.setTransformationStack(trans);

                gcode.writeLine(line.toString());
            }

            //Loop until we are not OK (end of data or found another command)
        }
    }

}
