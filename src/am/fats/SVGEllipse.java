package am.fats;

import org.xml.sax.Attributes;

import java.io.IOException;

public class SVGEllipse extends SVGElement
{
    public SVGEllipse()
    {
        mElementName = "ellipse";
    }

    @Override
    public void process(Attributes atts, FileLineWriter gcode, TransformationStack trans) throws IOException
    {
        //We need to extract the start X, Y and radius attributes.
        double startX      = 0;
        double startY      = 0;
        double radiusX     = 0;
        double radiusY     = 0;

        String attX = atts.getValue("cx");
        String attY = atts.getValue("cy");
        String radX = atts.getValue("rx");
        String radY = atts.getValue("ry");

        if(attX == null)
            attX = "0.0";
        if(attY == null)
            attY = "0.0";
        startX      = Double.parseDouble(attX);
        startY      = Double.parseDouble(attY);
        radiusX     = Double.parseDouble(radX);
        radiusY     = Double.parseDouble(radY);
        //We have all of the value from the attributes

        GCodeEllipse ellipse = new GCodeEllipse(startX, startY, radiusX, radiusY);
        ellipse.setTransformationStack(trans);

        gcode.writeLine(ellipse.toString());
    }
}
