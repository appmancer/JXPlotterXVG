#!/bin/bash

# Set up classpath with all dependencies
DEPS_DIR="build/deps"
CLASSPATH="$DEPS_DIR/slf4j-api-1.7.36.jar:$DEPS_DIR/logback-classic-1.2.11.jar:$DEPS_DIR/logback-core-1.2.11.jar:$DEPS_DIR/jaxb-api-2.3.1.jar:$DEPS_DIR/jaxb-runtime-2.3.1.jar:build/libs/JXPlotterSVG-1.4.0.jar"

# Run the application
if [ $# -eq 0 ]; then
    # No arguments - run in GUI mode
    java -cp "$CLASSPATH" am.fats.Main
else
    # Arguments provided - run in CLI mode
    java -cp "$CLASSPATH" am.fats.Main "$@"
fi