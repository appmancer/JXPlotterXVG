package am.fats;

import org.xml.sax.Attributes;
import java.io.FileWriter;
import java.io.IOException;

public class SVGElement
{
    protected String mElementName;
    protected boolean mIsOk;

    public SVGElement()
    {
        mIsOk = true;
    }

    public boolean acceptsElement(String elementName)
    {
        return mElementName.contentEquals(elementName);
    }

    protected String simplify(String input)
    {
        int changed = -1;
        String output = "";
        while(changed != 0) {
            output = input.replaceAll("\\s\\s", " ");
            changed = input.length() - output.length();

            input = output;
        }
        return output;
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
