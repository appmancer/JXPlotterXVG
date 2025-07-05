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

import java.io.IOException;
import java.util.ArrayDeque;

public class SVGPath_Line_Rel extends SVGPath_Line_Abs
{
    public SVGPath_Line_Rel()
    {}

    @Override
    public void process(ArrayDeque<String> commandQueue, FileLineWriter gcode, TransformationStack trans) throws IOException
    {
        //We need to reset mIsOk
        mIsOk = true;

        double x;
        double y;

        comment("line", gcode);
        //Head down if necessary
        headDown(gcode);

        while(mIsOk)
        {
            //Read x value
            x = readDouble(commandQueue);
            y = readDouble(commandQueue);

            if(mIsOk)
            {
                //Get the y value from the current state
                Point2D p = PlotterState.getLogicalPosition();

                //We have an X double from the command queue
                GCodeLine line = new GCodeLine(p.x + x, p.y + y);
                line.setTransformationStack(trans);

                gcode.writeLine(line.toString());
            }

            //Loop until we are not OK (end of data or found another command)
        }
    }
}
