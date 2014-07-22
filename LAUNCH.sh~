#!/bin/bash

NORMAL="\\033[0;39m"  # reset
RED="\\033[1;31m"

if [[ ! -d build ]]; then
	mkdir build
fi

# compilation
COMPIL="javac -cp . com/github/vmarquet/graph/*/*.java -d build"
echo -e "${RED}$COMPIL${NORMAL}"
eval "$COMPIL"

# execution
EXEC="java -cp ./build com.github.vmarquet.graph.test.Test100"
echo -e "${RED}$EXEC${NORMAL}"
eval "$EXEC"

exit 0
