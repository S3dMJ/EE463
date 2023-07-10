# The name of the class containing the "main" routine
MAINCLASS=WebServer

CLASSPATH=.:webserve.jar

build:
	javac -g -cp ${CLASSPATH} *.java

run: build
	java -ea -cp ${CLASSPATH} ${MAINCLASS}

clean:
	rm -f *.class

