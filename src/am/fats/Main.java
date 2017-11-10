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

public class Main {


    public static void main(String[] args) {
	    System.out.println("XPlotterSVG Copyright (c) 2017 Samuel Pickard");
        System.out.println("This program comes with ABSOLUTELY NO WARRANTY.");
        System.out.println("This is free software, and you are welcome to redistribute it");
        System.out.println("under certain conditions");

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


            //Open the output file
            FileWriter gcode;
            try
            {
                gcode = new FileWriter(outputFileName, false);
            }
            catch(Exception e)
            {
                System.out.print("Cannot open output file "); System.out.println(outputFileName);
                return;
            }

            //Create an instance of the parser

            //Parse!

            //Close the output file
            try {
                gcode.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            printUsage();;
            return false;
        }

        return true;
    }


}
