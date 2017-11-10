package am.fats;

import org.xml.sax.Attributes;

import java.io.IOException;

public class SVGDesc extends SVGElement
{
    public SVGDesc()
    {
        mElementName = "desc";
    }

    @Override
    public void process(Attributes atts, FileLineWriter gcode, TransformationStack trans) throws IOException
    {
        //TODO : The different SAX parser has broken this model
    }
}
