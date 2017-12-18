package am.fats;

public class GCodeDwell extends GCodeCommand
{
    protected int mDwell;

    public GCodeDwell(int dwell)
    {
        mDwell = dwell;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("( Pausing for ");
        builder.append(mDwell);
        builder.append(" seconds ) ");
        builder.append(System.lineSeparator());
        builder.append("G4 S");
        builder.append(mDwell);

        return builder.toString();
    }
}
