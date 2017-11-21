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

public class GCodeCommand
{
    protected TransformationStack mTrans;

    public void setTransformationStack(TransformationStack trans)
    {
        if(trans == null)
        {
            mTrans = new TransformationStack();
        }
        else
        {
            mTrans = trans.clone();
        }
    }

    public String toString()
    {
        //Virtual method
        return "";
    }
}
