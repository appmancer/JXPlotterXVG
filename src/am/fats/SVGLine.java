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

public class SVGLine extends SVGElement
{
    public SVGLine()
    {
        mElementName = "line";
    }

    @Override
    public void process(Attributes atts, FileLineWriter gcode) throws IOException
    {
        double startX    = 0;
        double startY    = 0;
        double endX      = 0;
        double endY      = 0;

        startX      = Double.parseDouble(atts.getValue("x1"));
        startY      = Double.parseDouble(atts.getValue("y1"));
        endX        = Double.parseDouble(atts.getValue("x2"));
        endY        = Double.parseDouble(atts.getValue("y2"));

        GCodeComment starting = new GCodeComment("Drawing line");
        gcode.writeLine(starting.toString());

        headUp(gcode);
        GCodeMove move = new GCodeMove(startX, startY);
        move.setTransformationStack(mTrans);
        gcode.writeLine(move.toString());

        headDown(gcode);
        GCodeLine line = new GCodeLine(endX, endY);
        line.setTransformationStack(mTrans);
        gcode.writeLine(line.toString());
    }

}
