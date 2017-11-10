package am.fats;

public class GCodeLaserOff extends GCodeCommand
{
    public GCodeLaserOff()
    {}

    @Override
    public String toString()
    {
        //Update the logical position of the plotter head
        PlotterState.setHeadUp();

        return "M5";
    }
}
