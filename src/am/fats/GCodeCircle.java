package am.fats;

public class GCodeCircle extends GCodePath
{
    protected double mX;
    protected double mY;
    protected double mRadius;

    public GCodeCircle(double x, double y, double radius)
    {
        mX = x;
        mY = y;
        mRadius = radius;
    }


    @Override
    public String toString()
    {
        //We need to convert a circle in to a series of short lines.  Some experimentation is
        //needed, but I'm going to assume that line lengths of 1mm are about right
        //Calculate the length of the circumference, and that will be the number of steps
        int steps = (int)Math.floor(2 * mRadius * Math.PI);


        //Fast mid-point circle algorithms aren't going to help us here, lets do it the old-fashioned way
        //Realtime processing is not a target, the user can wait for 1 second.
        //
        double radiansPerStep = Math.PI * 2 / steps;

        //Move to the zenith of the circle

        StringBuilder gcode = new StringBuilder();
        GCodeComment comment = new GCodeComment(String.format("Circle x:%.4f y:%.4f r:%.4f", mX, mY, mRadius));
        gcode.append(comment.toString());
        gcode.append(System.lineSeparator());

        gcode.append(toolUp());
        gcode.append(move(mX, mY + mRadius));
        gcode.append(toolDown());

        double theta = 0;
        for(int i = 0; i < steps; i++)
        {
            double nx = mX + mRadius * Math.sin(theta);
            double ny = mY + mRadius * Math.cos(theta);
            gcode.append(line(nx, ny));

            theta += radiansPerStep;
        }
        //Go back to the start
        gcode.append(line(mX, mY + mRadius));

        gcode.append(toolUp());

        return gcode.toString();
    }
}
