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

public class SVGPath_SmoothTo_Abs extends SVGPath_Curve_Abs
{
    public SVGPath_SmoothTo_Abs()
    {}

    @Override
    public void process(ArrayDeque<String> commandQueue, FileLineWriter gcode, TransformationStack trans) throws IOException
    {
        //We need to reset mIsOk
        mIsOk = true;

        mGcode = gcode;
        mTrans = trans.clone();
        Point2D startPoint = new Point2D();
        Point2D endPoint = new Point2D();
        Point2D startControlPoint = new Point2D();
        Point2D endControlPoint = new Point2D();

        comment("Starting smoothto", gcode);
        //Head down if necessary
        headDown(gcode);

        //Start point is the current head positon
        startPoint = PlotterState.getPosition();
        startControlPoint = PlotterState.getControlPoint();

        while(mIsOk)
        {
            endControlPoint.x = readDouble(commandQueue);
            endControlPoint.y = readDouble(commandQueue);
            endPoint.x = readDouble(commandQueue);
            endPoint.y = readDouble(commandQueue);

            if(mIsOk)
            {
                //I'm assuming that a 0,0 startControlPoint means that it hasn't been set
                //TODO: Well, this might actually be the control position!
                if(startControlPoint.x == 0 && startControlPoint.y == 0)
                {
                    startControlPoint = startPoint;
                }
                {
                    //We have to make the new control point the reflection of the original control point
                    startControlPoint.x = (-(startControlPoint.x - startPoint.x)) + startPoint.x;
                    startControlPoint.y = (-(startControlPoint.y - startPoint.y)) + startPoint.y;
                }

                double stepSize = 0.01; //Lets see how that goes.  This will mean that there will always be 100 steps per
                //curve.  Lets make it more sophisticated once its working
                //TODO: Calculate an appropriate resolution for each curve

                plotCurve(stepSize, startPoint, endPoint, startControlPoint, endControlPoint);

                startPoint = endPoint;
                startControlPoint = endControlPoint;
                //And save it for a following curve command
                PlotterState.setControlPosition(endControlPoint);
            }
        }
    }
}
