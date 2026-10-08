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

public class GCodeEllipse extends GCodePath
{
    protected double mX;
    protected double mY;
    protected double mRadiusX;
    protected double mRadiusY;

    public GCodeEllipse(double x, double y, double radiusX, double radiusY)
    {
        this.mX = x;
        this.mY = y;
        this.mRadiusX = radiusX;
        this.mRadiusY = radiusY;
    }

    @Override
    public String toString()
    {
        StringBuilder gcode = new StringBuilder();
        GCodeComment comment = new GCodeComment(String.format("Ellipse x:%.4f y:%.4f rX:%.4f rY:%.4f", mX, mY, mRadiusX, mRadiusY));
        gcode.append(comment.toString());
        gcode.append(System.lineSeparator());

        // If the ellipse is actually a circle (and transforms won't distort it), emit arcs.
        if(Math.abs(mRadiusX - mRadiusY) <= 1e-4 && (mTrans == null || mTrans.isUniformScaleAndNoSkew(1e-6)))
        {
            double r = (mRadiusX + mRadiusY) / 2.0;
            double startX = mX;
            double startY = mY + r;
            double oppositeX = mX;
            double oppositeY = mY - r;

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

        // Fallback: polygonize true ellipses.
        // Calculate the length of the circumference and use that as the number of steps.
        int steps = (int)Math.floor(2 * ((mRadiusX + mRadiusY) / 2) * Math.PI);
        double radiansPerStep = Math.PI * 2 / steps;

        gcode.append(toolUp());
        gcode.append(move(mX, mY + mRadiusY));
        gcode.append(toolDown());

        double theta = 0;
        for(int i = 0; i < steps; i++)
        {
            double nx = mX + mRadiusX * Math.sin(theta);
            double ny = mY + mRadiusY * Math.cos(theta);
            gcode.append(line(nx, ny));
            theta += radiansPerStep;
        }
        gcode.append(line(mX, mY + mRadiusY));

        gcode.append(toolUp());

        return gcode.toString();
    }
}
