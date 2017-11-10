package am.fats;

public class GCodeFeed extends GCodeCommand
{
    protected int mRate;

    public GCodeFeed(int rate)
    {
        mRate = rate;
    }

    @Override
    public String toString()
    {
        return String.format("G1 F%d", mRate);
    }
}
