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
    //Falcon 10W optical output; material power values are raw S values on a 0..1000 scale
    public static final double LASER_MAX_WATTS = 10.0;

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

    //Line energy delivered by a single pass, in J/mm: optical watts / (mm/s)
    public double lineEnergyPerPass()
    {
        if(mFeedrate == 0)
            return 0;

        double watts = (mPower / 1000.0) * LASER_MAX_WATTS;
        return watts / (mFeedrate / 60.0);
    }

    //Total line energy across all passes, in J/mm
    public double totalLineEnergy()
    {
        return lineEnergyPerPass() * mRepeat;
    }

    public void setTool(String tool)
    {
        if (tool == null) {
            // Default to pen if no tool is specified
            setTool(1);
            return;
        }
        
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
            else {
                // Default to pen for any other string
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
