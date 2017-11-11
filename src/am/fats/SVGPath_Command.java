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
import java.util.IllegalFormatException;

public class SVGPath_Command
{
    protected boolean mIsOk;
    public SVGPath_Command()
    {
        mIsOk = true;
    }

    protected double readDouble(ArrayDeque<String> commandQueue)
    {
        if(commandQueue.size() == 0)
        {
            mIsOk = false;
            return 0.0;
        }
        double retVal = 0;
        String nextValue = commandQueue.getFirst();
        try {
            retVal = Double.parseDouble(nextValue);
        }
        catch (NumberFormatException e)
        {
            mIsOk = false;
            return 0.0;
        }

        commandQueue.removeFirst();

        return retVal;
    }

    protected void comment(String text, FileLineWriter gcode) throws IOException {
        GCodeComment c = new GCodeComment(text);
        gcode.writeLine(c.toString());
    }

    protected void headUp(FileLineWriter gcode) throws IOException {
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

        gcode.writeLine(toolStr.toString());
    }

    protected void headDown(FileLineWriter gcode) throws IOException {
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

        gcode.writeLine(toolStr.toString());
    }


    public void process(ArrayDeque<String> commandQueue, FileLineWriter gcode, TransformationStack trans) throws IOException {
        //Virtual method
    }

}
