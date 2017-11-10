package am.fats;

public class GCodeLine extends GCodeMove
{
    public GCodeLine(double x, double y)
    {
        super(x, y);
    }

    @Override
    public String toString()
    {
        return toString(true);
    }

    public String toString(boolean translate)
    {
        Point2D translatedPoint = new Point2D(endX, endY);
        if(translate)
        {
            //Calculate the effect any translations will have on this point
            translatedPoint = mTrans.process(endX, endY);

            //Update the physical position of the print head
            PlotterState.setPosition(translatedPoint);

            //Update the logical position
            PlotterState.setLogicalPosition(endX, endY);
        }

        //We have to invert the y-coordinates to stop getting a mirror image
        double viewY = PlotterState.getViewBox().y;

        StringBuilder gcode = new StringBuilder();

        gcode.append("G1 X");
        gcode.append(String.format("%8f",translatedPoint.x));
        gcode.append(" Y");
        gcode.append(String.format("%8f", viewY - translatedPoint.y));
        //For Candle to show the rendering, we need to add a Z axis. The XPlotter ignores this value
        gcode.append(" Z -1.000");
        gcode.append(" F1200"); //Override feed rate, we're just moving
        gcode.append(System.lineSeparator());

        return gcode.toString();
    }
}
