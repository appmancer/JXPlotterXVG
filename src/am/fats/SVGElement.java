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

import org.xml.sax.Attributes;
import java.io.FileWriter;
import java.io.IOException;

public class SVGElement
{
    protected String mElementName;
    protected boolean mIsOk;

    public SVGElement()
    {
        mIsOk = true;
    }

    public boolean acceptsElement(String elementName)
    {
        return mElementName.contentEquals(elementName);
    }

    protected String simplify(String input)
    {
        int changed = -1;
        String output = "";
        while(changed != 0) {
            output = input.replaceAll("\\s\\s", " ");
            changed = input.length() - output.length();

            input = output;
        }
        return output;
    }

    public void headDown(FileWriter gcode)
    {

    }

    public void headUp(FileWriter gcode)
    {

    }

    public void process(Attributes atts, FileLineWriter gcode, TransformationStack trans) throws IOException {
        //Virtual method should be implemented
    }
}
