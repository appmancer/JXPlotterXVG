package am.fats;

import org.xml.sax.Attributes;

import java.io.IOException;

public class SVGGroup extends SVGElement
{
    public SVGGroup()
    {
        mElementName = "g";
    }

    @Override
    public void process(Attributes atts, FileLineWriter gcode, TransformationStack trans) throws IOException
    {
        //Deliberately blank
    }
}
