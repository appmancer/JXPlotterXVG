/*  XPlotterSVG - Convert SVG to GCode
    Copyright (C) 2017  Samuel Pickard
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>. */
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
        StringBuilder gcode = new StringBuilder();
        GCodeComment comment = new GCodeComment(String.format("Circle x:%.4f y:%.4f r:%.4f", mX, mY, mRadius));
        gcode.append(comment.toString());
        gcode.append(System.lineSeparator());

        // GRBL circular interpolation (G2/G3) cannot reliably do a full 360 in one command.
        // Emit two semicircles starting from the top of the circle.
        double startX = mX;
        double startY = mY + mRadius;
        double oppositeX = mX;
        double oppositeY = mY - mRadius;

        gcode.append(toolUp());
        gcode.append(move(startX, startY));
        gcode.append(toolDown());

        GCodeArc arc1 = new GCodeArc(oppositeX, oppositeY, mX, mY, false);
        arc1.setTransformationStack(mTrans.clone());
        gcode.append(arc1.toString());

        GCodeArc arc2 = new GCodeArc(startX, startY, mX, mY, false);
        arc2.setTransformationStack(mTrans.clone());
        gcode.append(arc2.toString());

        gcode.append(toolUp());

        return gcode.toString();
    }
}
