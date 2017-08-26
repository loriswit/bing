#!/usr/bin/env bash
set -E
cd $(dirname "$0")

trap 'echo; exit' ERR

echo -n "Building application... "

mvn -q install
cp target/bing-1.0-SNAPSHOT-jar-with-dependencies.jar bing.jar

echo "Done!"

java -jar bing.jar
