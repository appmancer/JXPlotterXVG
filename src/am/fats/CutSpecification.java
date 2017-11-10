package am.fats;

public class CutSpecification
{
    protected String mName;
    protected int mFeedrate;
    protected int mPower;
    protected int mRepeat;
    protected int mTool;
    protected String mHexCode;

    public CutSpecification(String name)
    {
        mName = name;
    }

    public void setName(String name)
    {
        mName = name;
    }

    public String getName()
    {
        return mName;
    }

    public void setFeedrate(int rate)
    {
        mFeedrate = rate;
    }

    public int getFeedrate()
    {
        return mFeedrate;
    }

    public void setPower(int power)
    {
        mPower = power;
    }

    public int getPower()
    {
        return mPower;
    }

    public void setRepeat(int repeat)
    {
        mRepeat = repeat;
    }

    public int getRepeat()
    {
        return mRepeat;
    }

    public void setTool(int tool)
    {
        mTool = tool;
    }

    public int getTool()
    {
        return mTool;
    }

    public void setHexCode(String hexcode)
    {
        mHexCode = hexcode;
    }

    public String getHexCode()
    {
        return mHexCode;
    }
}
