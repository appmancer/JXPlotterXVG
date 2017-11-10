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
