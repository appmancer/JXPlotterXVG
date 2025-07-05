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

public class Tool
{
    public static final int TOOL_PEN = 1;
    public static final int TOOL_LASER = 2;

    protected static int sPower = 100;
    protected static int sFeed = 200;
    protected static int sTool = TOOL_PEN;

    public static int currentTool()
    {
        return sTool;
    }

    public static void setTool(int tool)
    {
        sTool = tool;
    }

    public static int getPower()
    {
        return sPower;
    }

    public static void setPower(int power)
    {
        sPower = power;
    }

    public static int getFeedrate()
    {
        return sFeed;
    }

    public static void setFeedrate(int rate)
    {
        sFeed = rate;
    }
}
