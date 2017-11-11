package am.fats;

import org.xml.sax.Attributes;

import java.io.IOException;
import java.util.ArrayDeque;

public class SVGPath extends SVGElement
{
    public SVGPath()
    {
        mElementName = "path";
    }


    @Override
    public void process(Attributes atts, FileLineWriter gcode, TransformationStack trans) throws IOException
    {
        //a path element will typically hold a lot of movement data as a property.  Its the complete hack that holds SVG together
        //This data will contain commands and data.
        //   <path d="M 100 100 L 300 100 L 200 300 z" />

        String pathData = simplify(atts.getValue("d").trim());
        Point2D empty = new Point2D();
        PlotterState.setPosition(empty);
        PlotterState.setLogicalPosition(0, 0);

        //There is a great deal of variety in formatting this data.  These string are identical in meaning
        //C100,-100
        //C 100 100
        //C100-100
        //C 100, -100

        //We will have to parse the data character by character

        ArrayDeque<String> commandQueue = new ArrayDeque<String>();
        int c = 0;
        StringBuilder current = new StringBuilder();
        String commands = "MmCcSsZzLlHhVvQqTtAa";
        String delimiters = " ,";
        while(c < pathData.length())
        {
            Character nextChar = pathData.charAt(c++);
            if(commands.indexOf(nextChar) != -1)
            {
                //This is a new command
                if(current.length() > 0)
                {
                    commandQueue.add(current.toString());
                    current = new StringBuilder();
                }

                current.append(nextChar);
                if(current.length() > 0)
                    commandQueue.addLast(current.toString());
                current = new StringBuilder();
            }
            else if(delimiters.indexOf(nextChar) != -1)
            {
                if(current.length() > 0)
                    commandQueue.addLast(current.toString());

                current = new StringBuilder();
            }
            else if(nextChar == '-')
            {
                //Append the current string without adding nextChar
                if(current.toString().endsWith("e"))
                {
                    //We're processing an exponent, so this - is part of the current
                    current.append(nextChar);
                }
                else
                {
                    //We can send the current string to the queue, but we need to keep nextChar as part of the new string

                    if(current.length() > 0)
                        commandQueue.addLast(current.toString());

                    current = new StringBuilder();
                    current.append(nextChar);
                }
            }
            else if(Character.isDigit(nextChar) || nextChar == '.' || nextChar == 'e')
            {
                //We've got a number or a decimal point or and exponent
                current.append(nextChar);
            }
            //else ignore it and move on
        }
        if(current.length() > 0)
        {
            //One last one...
            if(current.length() > 0)
                commandQueue.addLast(current.toString());
        }

        //Process the queue
        String lastCommand = "";
        while(!commandQueue.isEmpty())
        {
            //Get the next command and remove it from the queue
            String nextCommand = commandQueue.getFirst();
                commandQueue.removeFirst();

            if(commands.contains(nextCommand))
            {
                lastCommand = nextCommand;
                switchCommand(commandQueue, nextCommand, gcode, trans.clone());
            }
            else
            {
                System.out.println(String.format("Unrecognised command: %s", nextCommand));
            }
        }

        GCodeComment endComment = new GCodeComment("Path complete");
        gcode.writeLine(endComment.toString());
    }
    
    protected void switchCommand(ArrayDeque<String> commandQueue, String currentCommand, FileLineWriter gcode, TransformationStack trans) throws IOException
    {
        if(currentCommand.contentEquals("M"))
        {
            SVGPath_Move_Abs mabs = new SVGPath_Move_Abs();
            mabs.process(commandQueue, gcode, trans);
        }
        else if(currentCommand.contentEquals("m"))
        {
            SVGPath_Move_Rel mrel = new SVGPath_Move_Rel();
            mrel.process(commandQueue, gcode, trans);
        }
        else if(currentCommand.toUpperCase().contentEquals("Z"))
        {
            SVGPath_ClosePath cpath = new SVGPath_ClosePath();
            cpath.process(commandQueue, gcode, trans);
        }
        if(currentCommand.contentEquals("L"))
        {
            SVGPath_Line_Abs labs = new SVGPath_Line_Abs();
            labs.process(commandQueue, gcode, trans);
        }
        else if(currentCommand.contentEquals("l"))
        {
            SVGPath_Line_Rel lrel = new SVGPath_Line_Rel();
            lrel.process(commandQueue, gcode, trans);
        }
        if(currentCommand.contentEquals("H"))
        {
            SVGPath_Horizontal_Line_Abs hlabs = new SVGPath_Horizontal_Line_Abs();
            hlabs.process(commandQueue, gcode, trans);
        }
        else if(currentCommand.contentEquals("h"))
        {
            SVGPath_Horizontal_Line_Rel hlrel = new SVGPath_Horizontal_Line_Rel();
            hlrel.process(commandQueue, gcode, trans);
        }
        if(currentCommand.contentEquals("V"))
        {
            SVGPath_Vertical_Line_Abs vlabs = new SVGPath_Vertical_Line_Abs();
            vlabs.process(commandQueue, gcode, trans);
        }
        else if(currentCommand.contentEquals("v"))
        {
            SVGPath_Vertical_Line_Rel vlrel = new SVGPath_Vertical_Line_Rel();
            vlrel.process(commandQueue, gcode, trans);
        }
        if(currentCommand.contentEquals("C"))
        {
            SVGPath_Curve_Abs cabs = new SVGPath_Curve_Abs();
            cabs.process(commandQueue, gcode, trans);
        }
        else if(currentCommand.contentEquals("c"))
        {
            SVGPath_Curve_Rel crel = new SVGPath_Curve_Rel();
            crel.process(commandQueue, gcode, trans);
        }
        if(currentCommand.contentEquals("S"))
        {
            SVGPath_SmoothTo_Abs sabs = new SVGPath_SmoothTo_Abs();
            sabs.process(commandQueue, gcode, trans);
        }
        else if(currentCommand.contentEquals("s"))
        {
            SVGPath_SmoothTo_Rel srel = new SVGPath_SmoothTo_Rel();
            srel.process(commandQueue, gcode, trans);
        }
        if(currentCommand.contentEquals("Q"))
        {
            SVGPath_Quadratic_Curve_Abs qcabs = new SVGPath_Quadratic_Curve_Abs();
            qcabs.process(commandQueue, gcode, trans);
        }
        else if(currentCommand.contentEquals("q"))
        {
            SVGPath_Quadratic_Curve_Rel qcrel = new SVGPath_Quadratic_Curve_Rel();
            qcrel.process(commandQueue, gcode, trans);
        }
        if(currentCommand.contentEquals("T"))
        {
            SVGPath_Quadratic_SmoothTo_Abs qsabs = new SVGPath_Quadratic_SmoothTo_Abs();
            qsabs.process(commandQueue, gcode, trans);
        }
        else if(currentCommand.contentEquals("t"))
        {
            SVGPath_Quadratic_SmoothTo_Rel qsrel = new SVGPath_Quadratic_SmoothTo_Rel();
            qsrel.process(commandQueue, gcode, trans);
        }
        if(currentCommand.contentEquals("A"))
        {
            SVGPath_Elliptical_Arc_Abs eaabs = new SVGPath_Elliptical_Arc_Abs();
            eaabs.process(commandQueue, gcode, trans);
        }
        else if(currentCommand.contentEquals("a"))
        {
            SVGPath_Elliptical_Arc_Rel earel = new SVGPath_Elliptical_Arc_Rel();
            earel.process(commandQueue, gcode, trans);
        }
    }
}
