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

public class GCodePath extends GCodeCommand
{
    public GCodePath()
    {}

    protected String toolUp()
    {
        StringBuilder toolStr = new StringBuilder();

        switch(Tool.currentTool())
        {
            case Tool.TOOL_PEN:
                GCodePenUp pen = new GCodePenUp();
                toolStr.append(pen.toString());
                break;
            case Tool.TOOL_LASER:
                GCodeLaserOff laser = new GCodeLaserOff();
                toolStr.append(laser.toString());
                break;
        }
        toolStr.append(System.lineSeparator());

        return toolStr.toString();
    }

    protected String toolDown()
    {
        StringBuilder toolStr = new StringBuilder();

        switch(Tool.currentTool())
        {
            case Tool.TOOL_PEN:
                GCodePenDown pen = new GCodePenDown();
                toolStr.append(pen.toString());
                break;
            case Tool.TOOL_LASER:
                GCodeLaserOn laser = new GCodeLaserOn();
                toolStr.append(laser.toString());
                break;
        }
        toolStr.append(System.lineSeparator());

        return toolStr.toString();
    }

    protected String move(double x, double y)
    {
        GCodeMove gmove = new GCodeMove(x, y);
        gmove.setTransformationStack(mTrans.clone());
        return gmove.toString();
    }

    protected String line(double x, double y)
    {
        GCodeLine gline = new GCodeLine(x, y);
        gline.setTransformationStack(mTrans.clone());
        return gline.toString();
    }
}
