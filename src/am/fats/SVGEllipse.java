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

import java.io.IOException;

public class SVGEllipse extends SVGElement
{
    public SVGEllipse()
    {
        mElementName = "ellipse";
    }

    @Override
    public void process(Attributes atts, FileLineWriter gcode) throws IOException
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
        ellipse.setTransformationStack(mTrans);

        gcode.writeLine(ellipse.toString());
    }
}
