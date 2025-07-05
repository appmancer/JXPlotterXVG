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

public class PlotterState
{

    protected static boolean sHeadDown = false;
    protected static Point2D sCurrentPosition = new Point2D(0, 0); //Not actually current position
    protected static Point2D sMarkedPosition = new Point2D(0, 0);  //Not actually marked
    protected static Point2D sControlPos = new Point2D(0, 0);      //Not actaully a control
    protected static Point2D sViewBox = new Point2D(200, 200);     //THIS IS THE VIEWBOX! (not used)
    protected static Point2D sLogicalPosition = new Point2D(0, 0); //Not actually logical

    public PlotterState()
    {}

    public static Point2D getPosition()
    {
        Point2D tmpPos = new Point2D(sCurrentPosition.x, sCurrentPosition.y);
        return tmpPos;
    }

    public static void setPosition(Point2D p)
    {
        sCurrentPosition.x = p.x;
        sCurrentPosition.y = p.y;
    }

    public static void setLogicalPosition(double x, double y)
    {
        sLogicalPosition.x = x;
        sLogicalPosition.y = y;
    }

    public static Point2D getLogicalPosition()
    {
        Point2D tmpPos = new Point2D(sLogicalPosition.x, sLogicalPosition.y);
        return tmpPos;
    }

    public static void setHeadDown()
    {
        sHeadDown = true;
    }

    public static void setHeadUp()
    {
        sHeadDown = false;
    }

    public static boolean isHeadDown()
    {
        return sHeadDown;
    }

    public static void markPosition()
    {
        sMarkedPosition.x = sLogicalPosition.x;
        sMarkedPosition.y = sLogicalPosition.y;
    }

    public static Point2D getMarkedPosition()
    {
        Point2D tmpPos = new Point2D(sMarkedPosition.x, sMarkedPosition.y);
        return tmpPos;
    }

    public static void setControlPosition(Point2D point)
    {
        sControlPos.x = point.x;
        sControlPos.y = point.y;
    }

    public static Point2D getControlPoint()
    {
        Point2D tmpPos = new Point2D(sControlPos.x, sControlPos.y);
        return tmpPos;
    }

    public static Point2D getViewBox()
    {
        Point2D tmpPos = new Point2D(sViewBox.x, sViewBox.y);
        return tmpPos;
    }

    public static void setViewBox(Point2D p)
    {
        sViewBox.x = p.x;
        sViewBox.y = p.y;
    }
}
