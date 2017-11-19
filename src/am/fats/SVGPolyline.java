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
import java.lang.reflect.Array;
import java.util.ArrayDeque;

public class SVGPolyline extends SVGElement
{
    protected Point2D mStartPoint;

    public SVGPolyline()
    {
        mElementName = "polyline";
    }

    public void process(Attributes atts, FileLineWriter gcode, TransformationStack trans) throws IOException
    {
        GCodeComment startComment = new GCodeComment("Starting Polyline");
        gcode.writeLine(startComment.toString());


        String polyData = simplify(atts.getValue("points"));
        String[] pointData = polyData.split(" ");
        //Convert the list of points into Point2D
        ArrayDeque<Point2D> points = new ArrayDeque<Point2D>();
        for(String pair : pointData)
        {
            String[] pointValues = pair.split(",");
            Point2D thisPoint = new Point2D();
            String x = pointValues[0];
            String y = pointValues[1];
            thisPoint.x = Double.parseDouble(x);
            thisPoint.y = Double.parseDouble(y);

            points.addLast(thisPoint);
        }

        if(mIsOk)
        {
            if(points.size() > 0)
            {
                //Move to the first point
                headUp(gcode);
                mStartPoint = points.getFirst();
                points.removeFirst();
                GCodeMove move = new GCodeMove(mStartPoint.x, mStartPoint.y);
                move.setTransformationStack(trans);
                gcode.writeLine(move.toString());
                headDown(gcode);
            }
            for(Point2D nextPoint : points)
            {
                GCodeLine line = new GCodeLine(nextPoint.x, nextPoint.y);
                line.setTransformationStack(trans);
                gcode.writeLine(line.toString());
            }
            headUp(gcode);
        }

    }
}
