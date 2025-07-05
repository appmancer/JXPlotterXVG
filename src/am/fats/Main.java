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
import org.xml.sax.XMLReader;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class
Main {


    public static void main(String[] args) throws IOException {

	    System.out.println("XPlotterSVG Copyright (c) 2018 Samuel Pickard");
        System.out.println("This program comes with ABSOLUTELY NO WARRANTY.");
        System.out.println("This is free software, and you are welcome to redistribute it");
        System.out.println("under certain conditions");
        System.out.print("Version ");
        System.out.println(Version.version());

        if(checkArgs(args.length))
        {
            String filePath = new File("").getAbsolutePath();
            String inputSVGFileName = args[0];
            String inputMaterialFileName = args[1];
            String outputFileName = "XPLOTTER.G";
            if(args.length > 2)
            {
                outputFileName = args[2];
            }

            //Parse the material file
            Material.load(inputMaterialFileName);

            //Open the output file
            FileLineWriter gcode;
            try
            {
                gcode = new FileLineWriter(outputFileName);
            }
            catch(Exception e)
            {
                System.out.print("Cannot open output file "); System.out.println(outputFileName);
                return;
            }

            //Check the nopause option
            Boolean pause = true;
            if(args.length > 3)
            {
                if(args[3].equals("--nopause"))
                {
                    pause = false;
                }
            }

            //Create an instance of the parser
            SVGParser parser = new SVGParser();

            //Parse!
            parser.process(inputSVGFileName, gcode, pause);

            //Close the output file
            try {
                gcode.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Complete.");
        }
    }

    protected static void printUsage()
    {
        System.out.println("Usage: XPlotterSVG <sourcefile> <materialfile> <outputfile>");
    }

    protected static boolean checkArgs(int argc)
    {
        if(argc < 3)
        {
            printUsage();
            return false;
        }

        return true;
    }


}
