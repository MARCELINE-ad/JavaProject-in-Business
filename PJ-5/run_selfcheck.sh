#!/usr/bin/env sh
set -eu
rm -rf out
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out ru.edu.pr02.SelfCheck "$@"
