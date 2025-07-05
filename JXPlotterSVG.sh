#!/bin/bash

# Determine the script directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

# Set up classpath with all dependencies
DEPS_DIR="$SCRIPT_DIR/deps"
CLASSPATH="$DEPS_DIR/slf4j-api-1.7.36.jar:$DEPS_DIR/logback-classic-1.2.11.jar:$DEPS_DIR/logback-core-1.2.11.jar:$DEPS_DIR/jaxb-api-2.3.1.jar:$DEPS_DIR/jaxb-runtime-2.3.1.jar:$SCRIPT_DIR/JXPlotterSVG-1.4.0.jar"

# Run the application
java -cp "$CLASSPATH" am.fats.Main "$@"