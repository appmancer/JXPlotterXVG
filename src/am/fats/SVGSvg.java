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

public class SVGSvg extends SVGElement
{
    public SVGSvg()
    {
        mElementName = "svg";
    }

    @Override
    public void process(Attributes atts, FileLineWriter gcode) throws IOException
    {
        String tempC = atts.getValue("viewBox");
        if(tempC == null || tempC.length() == 0)
        {
            tempC = "1 1 300 240";//Default XPlotter view
        }
        String[] dimensions = tempC.split(" ");
        if(dimensions.length == 4) {
            double rectWidth = Double.parseDouble(dimensions[2]);
            double rectHeight = Double.parseDouble(dimensions[3]);

            Point2D viewBox = new Point2D(rectWidth, rectHeight);

            PlotterState.setViewBox(viewBox);
        }
    }
}
