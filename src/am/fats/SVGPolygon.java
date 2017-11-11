package am.fats;

import org.xml.sax.Attributes;

import java.io.IOException;

public class SVGPolygon extends SVGPolyline
{
    public SVGPolygon()
    {
        mElementName = "polygon";
    }


    public void process(Attributes atts, FileLineWriter gcode, TransformationStack trans) throws IOException
    {
        super.process(atts, gcode, trans);

        //For the polygon we have to draw a line back to the start
        headDown(gcode);
        GCodeLine line = new GCodeLine(mStartPoint.x, mStartPoint.y);
        line.setTransformationStack(trans);
        gcode.writeLine(line.toString());
    }
}
