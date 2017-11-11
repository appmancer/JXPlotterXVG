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
