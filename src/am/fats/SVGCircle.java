package am.fats;

import org.xml.sax.Attributes;

import java.io.IOException;

public class SVGCircle extends SVGElement
{
    public SVGCircle()
    {
        mElementName = "circle";
    }

    @Override
    public void process(Attributes atts, FileLineWriter gcode, TransformationStack trans) throws IOException {
        //We need to extract the start X, Y and radius attributes.
        double startX      = 0;
        double startY      = 0;
        double radius      = 0;

        startX      = Double.parseDouble(atts.getValue("cx"));
        startY      = Double.parseDouble(atts.getValue("cy"));
        radius      = Double.parseDouble(atts.getValue("r"));
        //We have all of the value from the attributes
        GCodeCircle circle = new GCodeCircle(startX, startY, radius);
        circle.setTransformationStack(trans);

        gcode.writeLine(circle.toString());
    }
}
