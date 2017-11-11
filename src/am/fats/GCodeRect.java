package am.fats;

public class GCodeRect extends GCodePath
{
    protected double startX      = 0;
    protected double startY      = 0;
    protected double rectWidth   = 0;
    protected double rectHeight  = 0;

    public GCodeRect(double x, double y, double rectWidth, double rectHeight)
    {
        startX = x;
        startY = y;
        this.rectHeight = rectHeight;
        this.rectWidth = rectWidth;
    }

    @Override
    public String toString()
    {
        StringBuilder gcode = new StringBuilder();
        gcode.append(toolUp());
        gcode.append(move(startX, startY));
        gcode.append(toolDown());
        gcode.append(line(startX + rectWidth, startY));
        gcode.append(line(startX + rectWidth, startY + rectHeight));
        gcode.append(line(startX,startY + rectHeight));
        gcode.append(line(startX, startY));
        gcode.append(toolUp());

        return gcode.toString();
    }

}
