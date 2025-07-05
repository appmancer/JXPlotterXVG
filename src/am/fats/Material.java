/*  XPlotterSVG - Convert SVG to GCode
    Copyright (C) 2017  Samuel Pickard
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>. */
package am.fats;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

public class Material extends DefaultHandler
{
    protected static ArrayDeque<CutSpecification> sSpecs;
    protected static String sName;

    public static void load (String materialFileName)
    {
        sSpecs = new ArrayDeque<CutSpecification>();

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
            if(atts.getValue("feedrate") != null) {
                //This assumes that if we have feedrate, then we will have the other values as well
                newSpec.setFeedrate(Integer.parseInt(atts.getValue("feedrate")));
                newSpec.setPower(Integer.parseInt(atts.getValue("power")));
                newSpec.setRepeat(Integer.parseInt(atts.getValue("repeat")));
            }
            if(atts.getValue("dwell") != null)
            {
                newSpec.setDwell(Integer.parseInt(atts.getValue("dwell")));
            }

            newSpec.setTool(atts.getValue("tool"));
            newSpec.setHexCode(atts.getValue("hexcode"));

            sSpecs.add(newSpec);
        }
    }

    public static ArrayList<CutSpecification> getSpecification(String hexcode)
    {
        ArrayList<CutSpecification> returnArray = new ArrayList<CutSpecification>();
        for(CutSpecification cut : sSpecs)
        {
            if(cut.getHexCode().equals(hexcode))
            {
                returnArray.add(cut);
            }
        }
        return returnArray;
    }
}
