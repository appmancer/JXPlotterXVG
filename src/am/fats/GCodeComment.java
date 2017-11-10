package am.fats;

public class GCodeComment extends GCodeCommand
{
    String mComment = "";

    public GCodeComment(String comment)
    {
        mComment = comment;
    }

    @Override
    public String toString()
    {
        return String.format("( %s )", mComment);
    }
}
