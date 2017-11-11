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

public class GCodeMove extends GCodeCommand
{
    protected double endX;
    protected double endY;

    public GCodeMove(double x, double y)
    {
        this.endX = x;
        this.endY = y;
    }

    @Override
    public String toString()
    {
        //Calculate the effect any translations will have on this point
        Point2D translatedPoint = mTrans.process(endX, endY);

        //Update our physical position of the print head
        PlotterState.setPosition(translatedPoint);

        //Update the logical position
        PlotterState.setLogicalPosition(endX, endY);

        //One of the differences between move and line, is that we remember the position of the last move
        //so if we get a command to close the shape, we can draw a line back to the point we last
        //moved to.
        PlotterState.markPosition();

        //If we've moved then any chained control points are invalid
        Point2D empty = new Point2D(0, 0);
        PlotterState.setControlPosition(empty);

        //We have to invert the y-coordinates to stop getting a mirror image
        double viewY = PlotterState.getViewBox().y;

        StringBuilder gcode = new StringBuilder();

        gcode.append("G0 X");
        gcode.append(String.format("%8f",translatedPoint.x));
        gcode.append(" Y");
        gcode.append(String.format("%8f", viewY - translatedPoint.y));
        //For Candle to show the rendering, we need to add a Z axis. The XPlotter ignores this value
        gcode.append(" Z -1.000");
        gcode.append(" F1200"); //Override feed rate, we're just moving
        gcode.append(System.lineSeparator());

        return gcode.toString();
    }
}



