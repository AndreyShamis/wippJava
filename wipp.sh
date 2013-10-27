#!/bin/bash
cp -r Scripts dist/
cd dist
java -jar "wippJava.jar" &
