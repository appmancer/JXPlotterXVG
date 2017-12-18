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

public class CutSpecification
{
    protected String mName;
    protected int mFeedrate;
    protected int mPower;
    protected int mRepeat;
    protected int mTool;
    protected int mDwell;
    protected String mHexCode;

    public CutSpecification(String name)
    {
        mName = name;
        mDwell = 0;
    }

    public void setName(String name)
    {
        mName = name;
    }

    public String getName()
    {
        return mName;
    }

    public void setFeedrate(int rate)
    {
        mFeedrate = rate;
    }

    public int getFeedrate()
    {
        return mFeedrate;
    }

    public void setPower(int power)
    {
        mPower = power;
    }

    public int getPower()
    {
        return mPower;
    }

    public void setRepeat(int repeat)
    {
        mRepeat = repeat;
    }

    public int getRepeat()
    {
        return mRepeat;
    }

    public void setDwell(int dwell)
    {
        mDwell = dwell;
    }

    public int getDwell()
    {
        return mDwell;
    }

    public void setTool(String tool)
    {
        try {
            int t = Integer.parseInt(tool);
            setTool(t);
        }
        catch(NumberFormatException nfe)
        {
            if(tool.toLowerCase().contentEquals("laser"))
            {
                setTool(2);
            }
            else if(tool.toLowerCase().contentEquals("pen")
                    || tool.toLowerCase().contentEquals("brush"))
            {
                setTool(1);
            }
        }
    }

    public void setTool(int tool)
    {
        mTool = tool;
    }

    public int getTool()
    {
        return mTool;
    }

    public void setHexCode(String hexcode)
    {
        mHexCode = hexcode;
    }

    public String getHexCode()
    {
        return mHexCode;
    }
}
