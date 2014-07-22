#!/bin/bash

NORMAL="\\033[0;39m"  # reset
BLUE="\e[1;34m"

# on crée un dossier build pour ne pas mélanger les .java et les .class
if [[ ! -d build ]]; then
	mkdir build
fi

# compilation
COMPIL="javac -cp . com/github/vmarquet/graph/*/*.java -d build"
echo -e "${BLUE}$COMPIL${NORMAL}"
eval "$COMPIL"

# we check if compilation was successful, and if yes, we launch the program
if [[ $? -eq 0 ]]; then
	EXEC="java -cp ./build com.github.vmarquet.graph.test.Test100"
	echo -e "${BLUE}$EXEC${NORMAL}"
	eval "$EXEC"
fi

exit 0
