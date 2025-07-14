#!/bin/bash

# Distorio Image Editor Launcher Script

echo "Starting Distorio Image Editor..."
echo "Building project..."

# Build the project
mvn clean install -q

if [ $? -eq 0 ]; then
    echo "Build successful! Launching application..."
    cd distorio-app
    mvn exec:java -Dexec.mainClass="io.distorio.app.DistorioApp"
else
    echo "Build failed! Please check the error messages above."
    exit 1
fi 