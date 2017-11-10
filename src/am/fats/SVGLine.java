package am.fats;

import org.xml.sax.Attributes;

import java.io.IOException;

public class SVGLine extends SVGElement
{
    public SVGLine()
    {
        mElementName = "line";
    }

    @Override
    public void process(Attributes atts, FileLineWriter gcode, TransformationStack trans) throws IOException
    {
        double startX    = 0;
        double startY    = 0;
        double endX      = 0;
        double endY      = 0;

        startX      = Double.parseDouble(atts.getValue("x1"));
        startY      = Double.parseDouble(atts.getValue("y1"));
        endX        = Double.parseDouble(atts.getValue("x2"));
        endY        = Double.parseDouble(atts.getValue("y2"));

        GCodeComment starting = new GCodeComment("Drawing line");
        gcode.writeLine(starting.toString());

        headUp(gcode);
        GCodeMove move = new GCodeMove(startX, startY);
        move.setTransformationStack(trans.clone());
        gcode.writeLine(move.toString());

        headDown(gcode);
        GCodeLine line = new GCodeLine(endX, endY);
        line.setTransformationStack(trans.clone());
        gcode.writeLine(line.toString());
    }

}
