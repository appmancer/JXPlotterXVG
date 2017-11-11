package am.fats;

import org.xml.sax.Attributes;

import java.io.IOException;

public class SVGRect extends SVGElement
{
    public SVGRect()
    {
        mElementName = "rect";
    }

    public void process(Attributes atts, FileLineWriter gcode, TransformationStack trans) throws IOException
    {
        double startX      = 0;
        double startY      = 0;
        double rectWidth   = 0;
        double rectHeight  = 0;

        startX      = Double.parseDouble(atts.getValue("x"));
        startY      = Double.parseDouble(atts.getValue("y"));
        rectWidth   = Double.parseDouble(atts.getValue("width"));
        rectHeight  = Double.parseDouble(atts.getValue("height"));

        //We have all of the value from the attributes
        GCodeRect rect = new GCodeRect(startX, startY, rectWidth, rectHeight);
        rect.setTransformationStack(trans);
        gcode.writeLine(rect.toString());
    }
}
