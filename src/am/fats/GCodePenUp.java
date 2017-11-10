package am.fats;

public class GCodePenUp extends GCodeCommand
{
    public GCodePenUp()
    {}

    @Override
    public String toString()
    {
        //Update the logical position of the plotter head
        PlotterState.setHeadUp();

        return "M300S170";
    }
}
