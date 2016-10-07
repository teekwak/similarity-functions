#!/bin/bash

javac Initializer.java
java Initializer

rm *.class 2> /dev/null
rm DataAnalysis/*.class 2> /dev/null
rm RaspberryPi/*.class 2> /dev/null
rm Server/*.class 2> /dev/null