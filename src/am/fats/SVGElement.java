package am.fats;

import org.xml.sax.Attributes;
import java.io.FileWriter;
import java.io.IOException;

public class SVGElement
{
    protected String mElementName;

    public SVGElement()
    {
    }

    public boolean acceptsElement(String mElementName)
    {
        return mElementName.contentEquals(mElementName);
    }

    public String findAttributeValue(String attributeName)
    {
        String at = "";

        return at;
    }

    public void headDown(FileWriter gcode)
    {

    }

    public void headUp(FileWriter gcode)
    {

    }

    public void process(Attributes atts, FileLineWriter gcode, TransformationStack trans) throws IOException {
        //Virtual method should be implemented
    }
}
