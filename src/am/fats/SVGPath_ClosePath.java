package am.fats;

import java.io.IOException;
import java.util.ArrayDeque;

public class SVGPath_ClosePath extends SVGPath_Command
{
    public SVGPath_ClosePath()
    {

    }

    @Override
    public void process(ArrayDeque<String> commandQueue, FileLineWriter gcode, TransformationStack trans) throws IOException
    {
        comment("Closing path", gcode);

        //Head down if necessary
        headDown(gcode);

        Point2D p = PlotterState.getMarkedPosition();
        GCodeLine line = new GCodeLine(p.x, p.y);
        line.setTransformationStack(trans);
        gcode.writeLine(line.toString());

        //Any chained control point is now invalid
        Point2D empty = new Point2D();
        PlotterState.setControlPosition(empty);
    }
}
