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

import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileWriter;
import java.io.IOException;

public class SVGParser extends DefaultHandler
{

    protected FileLineWriter mGCode;
    protected TransformationStack mTrans;
    protected SVGElement[] mElements = { new SVGCircle(), new SVGEllipse(), new SVGPolyline(), new SVGPath(),
                        new SVGGroup(), new SVGLine(), new SVGPolygon(), new SVGRect(), new SVGImage()};
    protected SVGSvg mSVGElement = new SVGSvg();

    public void process(String inputSVGFileName, FileLineWriter gcode) throws IOException
    {
        mGCode = gcode;
        mTrans = new TransformationStack();

        //We have an open SVG.  Write out the standard header
        mGCode.writeLine("M0"); //Disable screen
        mGCode.writeLine("G21"); //Units in mm
        mGCode.writeLine("G28"); //Reset to origin
        mGCode.writeLine("G90"); //All positions are absolute

        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser saxParser;
        org.xml.sax.XMLReader svgReader;
        try {
            saxParser = spf.newSAXParser();

            //Open the input SVG
            svgReader = saxParser.getXMLReader();
            try {
                svgReader.setContentHandler(this);
                svgReader.parse(inputSVGFileName);
            } catch (IOException e) {
                System.out.print("Cannot open SVG file "); System.out.println(inputSVGFileName);
                e.printStackTrace();
                return;
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        //Finish the gcode
        //Switch off tool
        mGCode.writeLine("G28"); //Reset to origin

        //enable screen
        mGCode.writeLine("M1");
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
    {
        //Special case for an SVG element to get the viewbox dimensions
        if(localName.contentEquals("svg"))
        {
            try {
                mSVGElement.process(atts, mGCode, mTrans);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        CutSpecification spec = findSpecfication(atts);
        TransformationStack thisTrans = mTrans.clone();
        //Get any transformations that need to be applied
        String transformationData = atts.getValue("transform");
        if(transformationData == null)
            transformationData = "";
        if(transformationData.trim().length() > 0)
        {
            TransformationParser tparser = new TransformationParser();
            thisTrans = tparser.process(transformationData, thisTrans);
        }

        //Another special case - TODO: redesign
        if(localName.toLowerCase().contentEquals("image"))
        {
            spec = Material.getSpecification("raster");
        }

        if(spec != null)
        {
            for(SVGElement elem : mElements)
            {
                if(elem.acceptsElement(localName))
                {
                    try {
                        configureTool(spec);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    for(int i=0; i < spec.getRepeat(); i++)
                    {
                        try {
                            elem.process(atts, mGCode, thisTrans.clone());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    protected CutSpecification findSpecfication(Attributes atts)
    {
        CutSpecification spec = null;
        String hexcode = atts.getValue("stroke");
        if(hexcode == null)
            hexcode = "";
        if(hexcode.length() > 0)
        {
            spec = Material.getSpecification(hexcode);
        }
        else
        {
            String style = atts.getValue("style");
            if(style == null)
                style = "";
            if(style.length() > 0)
            {
                String[] styles = style.split(";");
                for(String harry : styles)
                {
                    String[] nvp = harry.split(":");
                    if(nvp[0].contentEquals("stroke"))
                    {
                        spec = Material.getSpecification(nvp[1]);
                    }
                }
            }
        }
        return spec;
    }

    protected void configureTool(CutSpecification spec) throws IOException {
        if(spec == null)
            return;

        GCodeComment comment = new GCodeComment("Setting feed rate");
        mGCode.writeLine(comment.toString());

        GCodeFeed feed = new GCodeFeed(spec.getFeedrate());
        mGCode.writeLine(feed.toString());


        //Set the tool
        Tool.setTool(spec.getTool());

        //Set the power (laser only)
        Tool.setPower(spec.getPower());

        //Set the feedrate
        Tool.setFeedrate(spec.getFeedrate());
    }
}
