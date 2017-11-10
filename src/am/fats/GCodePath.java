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
