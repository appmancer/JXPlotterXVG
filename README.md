# JXPlotterSVG

This is a Java application to convert SVG files into GCode, specifically for the [XPlotter](https://www.pinecone.ai/product-page/xplotter-kit).

## Features

- Converts SVG files to GCode for XPlotter devices
- Supports various SVG elements (paths, lines, circles, etc.)
- Configurable material settings for different tools and media
- Raster image support for laser engraving photos
- Boundary box preview to verify material placement
- GUI interface for easier operation

## Requirements

- Java 11 or higher

## Building from Source

### Using the Build Script (Recommended)

The easiest way to build JXPlotterSVG is using the included build script:

```bash
# Clone the repository
git clone https://github.com/yourusername/JXPlotterXVG.git
cd JXPlotterXVG

# Make the build script executable
chmod +x build.sh

# Build the application
./build.sh
```

The build script will:
1. Download all required dependencies
2. Compile the Java source files
3. Create a runnable JAR file

The built JAR file will be in `build/libs/JXPlotterSVG-1.4.0.jar`.

### Using Gradle (Alternative)

```bash
# Build with Gradle
./gradlew build

# Create a runnable JAR file
./gradlew jar

# Create a native application package (optional)
./gradlew jpackage
```

## Usage

### Using the Standalone JAR

The simplest way to run JXPlotterSVG is using the standalone JAR file:

```bash
# Run in GUI mode (no arguments)
java -jar JXPlotterSVG-1.4.0.jar

# Run in CLI mode with arguments
java -jar JXPlotterSVG-1.4.0.jar MyDesign.svg laser+card.xml XPLOTTER.G
```

### Using the Wrapper Script

You can also use the included wrapper script:

```bash
# Make the wrapper script executable
chmod +x JXPlotterSVG.sh

# Run in GUI mode (no arguments)
./JXPlotterSVG.sh

# Run in CLI mode with arguments
./JXPlotterSVG.sh MyDesign.svg laser+card.xml XPLOTTER.G
```

### Using the Run Script

Alternatively, you can use the run.sh script:

```bash
# Make the run script executable
chmod +x run.sh

# Run in GUI mode (no arguments)
./run.sh

# Run in CLI mode with arguments
./run.sh MyDesign.svg laser+card.xml XPLOTTER.G
```

### Graphical User Interface (GUI)

The application includes a graphical interface for easier use:

1. Launch the application by running `./run.sh` without arguments
2. Use the "Browse" buttons to select your SVG file, material file, and output location
3. Check "Disable Pause Button" if needed
4. Click "Convert" to process the file
5. The log area will show progress and any errors

### Command-line Interface (CLI)

You can use the command-line interface for scripting or automation:

```bash
./run.sh <svgfile> <materialfile> <outputfile> [--nopause]
```

For example:
```bash
./run.sh MyDesign.svg laser+card.xml XPLOTTER.G
```

#### Command-line options

- `<svgfile>`: Path to the SVG file to convert
- `<materialfile>`: Path to the material configuration XML file
- `<outputfile>`: (Optional) Path for the output GCode file (default: XPLOTTER.G)
- `--nopause`: (Optional) Disable the pause button in the generated GCode

## Material Files

Material files define how the XPlotter should operate with different tools and media. They are XML files with settings for feed rate, power, and repetition.

Example material file:
```xml
<?xml version="1.0" standalone="no"?>
<material name="Laser and Card">
    <Light   feedrate="800" power="32"  repeat="1" hexcode="#ff0000" tool="laser"/>
    <Medium  feedrate="400" power="64"  repeat="1" hexcode="#0000ff" tool="laser"/>
    <Dark    feedrate="200" power="128" repeat="1" hexcode="#00ff00" tool="laser"/>
    <Cut     feedrate="200" power="255" repeat="1" hexcode="#000000" tool="laser"/>
    <Raster  feedrate="400" power="255" repeat="1" hexcode="raster"  tool="laser"/>
</material>
```

## Creating SVG Files

1. Design your SVG in an editor like [Inkscape](https://inkscape.org/)
2. Use the [template](https://fats.am/XPlotterSVG/XPlotterTemplate.svg) for proper dimensions (240x300mm)
3. Set stroke colors to match the hexcodes in your material file
4. For text, convert to paths (Path > Object to Path in Inkscape)
5. Save as Plain SVG

## New in Version 1.4.0

- Updated build system for Java 11+ compatibility
- Added boundary box preview to verify material placement
- Improved error handling and logging
- Fixed JAXB dependency issues for modern Java versions
- Added build.sh and run.sh scripts for easier building and execution
- Created standalone JAR with proper manifest
- Added JXPlotterSVG.sh wrapper script for easier execution
- Created standalone distribution with all dependencies

## License

This project is licensed under the GNU General Public License v3.0 - see the LICENSE file for details.