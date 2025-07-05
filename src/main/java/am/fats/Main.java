/*  XPlotterSVG - Convert SVG to GCode
    Copyright (C) 2017-2025  Samuel Pickard
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("JXPlotterSVG version {} starting", Version.version());
        System.out.println("JXPlotterSVG Copyright (c) 2017-2025 Samuel Pickard");
        System.out.println("This program comes with ABSOLUTELY NO WARRANTY.");
        System.out.println("This is free software, and you are welcome to redistribute it");
        System.out.println("under certain conditions");
        System.out.print("Version ");
        System.out.println(Version.version());

        if (!validateArguments(args)) {
            return;
        }

        try {
            processFiles(args);
        } catch (Exception e) {
            logger.error("Error processing files", e);
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static boolean validateArguments(String[] args) {
        if (args.length < 2) {
            printUsage();
            return false;
        }

        // Validate input SVG file exists
        Path svgPath = Paths.get(args[0]);
        if (!Files.exists(svgPath)) {
            System.err.println("Error: SVG file not found: " + args[0]);
            return false;
        }

        // Validate material file exists
        Path materialPath = Paths.get(args[1]);
        if (!Files.exists(materialPath)) {
            System.err.println("Error: Material file not found: " + args[1]);
            return false;
        }

        return true;
    }

    private static void processFiles(String[] args) throws IOException {
        String inputSVGFileName = args[0];
        String inputMaterialFileName = args[1];
        String outputFileName = args.length > 2 ? args[2] : "XPLOTTER.G";

        // Parse the material file
        logger.info("Loading material file: {}", inputMaterialFileName);
        Material.load(inputMaterialFileName);

        // Open the output file
        logger.info("Creating output file: {}", outputFileName);
        try (FileLineWriter gcode = new FileLineWriter(outputFileName)) {
            // Check the nopause option
            boolean pause = true;
            if (args.length > 3 && args[3].equals("--nopause")) {
                pause = false;
            }

            // Create an instance of the parser
            SVGParser parser = new SVGParser();

            // Parse!
            logger.info("Processing SVG file: {}", inputSVGFileName);
            parser.process(inputSVGFileName, gcode, pause);

            logger.info("Processing complete");
            System.out.println("Complete.");
        } catch (IOException e) {
            logger.error("Failed to process files", e);
            System.err.println("Error: Cannot process files: " + e.getMessage());
            throw e;
        }
    }

    private static void printUsage() {
        System.out.println("Usage: JXPlotterSVG <sourcefile> <materialfile> [<outputfile>] [--nopause]");
        System.out.println();
        System.out.println("  <sourcefile>   - Path to SVG file to convert");
        System.out.println("  <materialfile> - Path to XML material specification file");
        System.out.println("  <outputfile>   - Optional: Path for output GCode file (default: XPLOTTER.G)");
        System.out.println("  --nopause      - Optional: Disable the pause button in generated GCode");
    }
}