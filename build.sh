#!/bin/bash

# Simple build script for JXPlotterSVG
echo "Building JXPlotterSVG..."

# Create directories
mkdir -p build/classes
mkdir -p build/libs
mkdir -p build/deps

# Download dependencies
echo "Downloading dependencies..."
DEPS_DIR="build/deps"
wget -q -O "$DEPS_DIR/slf4j-api-1.7.36.jar" "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar"
wget -q -O "$DEPS_DIR/logback-classic-1.2.11.jar" "https://repo1.maven.org/maven2/ch/qos/logback/logback-classic/1.2.11/logback-classic-1.2.11.jar"
wget -q -O "$DEPS_DIR/logback-core-1.2.11.jar" "https://repo1.maven.org/maven2/ch/qos/logback/logback-core/1.2.11/logback-core-1.2.11.jar"
wget -q -O "$DEPS_DIR/jaxb-api-2.3.1.jar" "https://repo1.maven.org/maven2/javax/xml/bind/jaxb-api/2.3.1/jaxb-api-2.3.1.jar"
wget -q -O "$DEPS_DIR/jaxb-runtime-2.3.1.jar" "https://repo1.maven.org/maven2/org/glassfish/jaxb/jaxb-runtime/2.3.1/jaxb-runtime-2.3.1.jar"

# Set classpath for compilation
CLASSPATH="$DEPS_DIR/slf4j-api-1.7.36.jar:$DEPS_DIR/logback-classic-1.2.11.jar:$DEPS_DIR/logback-core-1.2.11.jar:$DEPS_DIR/jaxb-api-2.3.1.jar:$DEPS_DIR/jaxb-runtime-2.3.1.jar"

# Compile Java files
echo "Compiling Java files..."
find src/am/fats -name "*.java" > sources.txt
javac -cp "$CLASSPATH" -d build/classes @sources.txt

# Create JAR file
echo "Creating JAR file..."
jar -cvf build/libs/JXPlotterSVG-1.4.0.jar -C build/classes .

echo "Build complete. JAR file is at build/libs/JXPlotterSVG-1.4.0.jar"
echo "Run with: java -cp \"$CLASSPATH:build/libs/JXPlotterSVG-1.4.0.jar\" am.fats.Main"