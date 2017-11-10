package am.fats;

public class GCodePenDown extends GCodeCommand
{
    public GCodePenDown()
    {}

    @Override
    public String toString()
    {
        PlotterState.setHeadDown();
        return "M300S90";
    }
}

