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

public class Point2D {
    public double x;
    public double y;

    public Point2D()
    {
        this.x = 0;
        this.y = 0;
    }

    public Point2D(double nx, double ny)
    {
        this.x = nx;
        this.y = ny;
    }

    public Point2D(Point2D copy)
    {
        this.x = copy.x;
        this.y = copy.y;
    }
}
