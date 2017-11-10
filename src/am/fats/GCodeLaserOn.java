package am.fats;

public class GCodeLaserOn extends GCodeCommand
{
    public GCodeLaserOn()
    {}

    @Override
    public String toString()
    {
        //Update the logical position of the plotter head
        PlotterState.setHeadDown();

        return String.format("M3 S%d", Tool.getPower());
    }
}
