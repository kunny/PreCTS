#!/bin/bash

# Create build.xml
android create uitest-project -n PreCtsAutomator -t 1 -p .

# build
ant build

# copy jar
echo Copying jar...
mv bin/PreCtsAutomator.jar ../PreCtsUI/automation/PreCtsAutomator.jar

echo Complete
