package am.fats;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.HashMap;

public class Material extends DefaultHandler
{
    protected static HashMap<String, CutSpecification> sSpecs;
    protected static String sName;

    public static void load (String materialFileName)
    {
        sSpecs = new HashMap<String, CutSpecification>();

        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser saxParser;
        org.xml.sax.XMLReader svgReader;
        try {
            saxParser = spf.newSAXParser();

            //Open the material file
            svgReader = saxParser.getXMLReader();
            try {
                svgReader.setContentHandler(new Material());
                svgReader.parse(materialFileName);
            } catch (IOException e) {
                System.out.print("Cannot open material file "); System.out.println(materialFileName);
                e.printStackTrace();
                return;
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }


    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
    {
        if(localName.contentEquals("material"))
        {
            //New material
            sName = localName;
        }
        else
        {
            //Other start elements name cut specifications
            CutSpecification newSpec = new CutSpecification(localName);
            //Get the properties from the attributes
            newSpec.setFeedrate(Integer.parseInt(atts.getValue("feedrate")));
            newSpec.setPower(Integer.parseInt(atts.getValue("power")));
            newSpec.setRepeat(Integer.parseInt(atts.getValue("repeat")));
            newSpec.setTool(Integer.parseInt(atts.getValue("tool")));
            newSpec.setHexCode(atts.getValue("hexcode"));

            sSpecs.put(atts.getValue("hexcode"), newSpec);
        }
    }

    public static CutSpecification getSpecification(String hexcode)
    {
        return sSpecs.get(hexcode);
    }
}
