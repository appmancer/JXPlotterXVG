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
    You should have received a copy of the GNU General General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>. */
package am.fats;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class GCodeArc extends GCodeCommand
{
    protected double endX;
    protected double endY;
    protected double centerX;
    protected double centerY;
    protected boolean clockwise;

    public GCodeArc(double endX, double endY, double centerX, double centerY, boolean clockwise)
    {
        this.endX = endX;
        this.endY = endY;
        this.centerX = centerX;
        this.centerY = centerY;
        this.clockwise = clockwise;
    }

    @Override
    public String toString()
    {
        Point2D startLogical = PlotterState.getLogicalPosition();

        Point2D endTranslated = mTrans.process(endX, endY);
        Point2D centerTranslated = mTrans.process(centerX, centerY);
        Point2D startTranslated = mTrans.process(startLogical.x, startLogical.y);

        PlotterState.setPosition(endTranslated);
        PlotterState.setLogicalPosition(endX, endY);

        double viewY = PlotterState.getViewBox().y;

        double xOut = endTranslated.x;
        double yOut = viewY - endTranslated.y;

        double iOut = centerTranslated.x - startTranslated.x;
        double jOut = -(centerTranslated.y - startTranslated.y);

        Locale currentLocale = Locale.getDefault();
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(currentLocale);
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("0.####", otherSymbols);
        df.setGroupingUsed(false);

        StringBuilder gcode = new StringBuilder();
        gcode.append(clockwise ? "G2" : "G3");
        gcode.append(" X");
        gcode.append(df.format(xOut));
        gcode.append(" Y");
        gcode.append(df.format(yOut));
        gcode.append(" I");
        gcode.append(df.format(iOut));
        gcode.append(" J");
        gcode.append(df.format(jOut));
        gcode.append(String.format(" F%d", Tool.getFeedrate()));
        gcode.append(System.lineSeparator());

        return gcode.toString();
    }
}
