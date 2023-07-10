@echo off
cls
set fn=WebServer.class
if not exist %fn% (
  javac -g -classpath .;webserve.jar; -d . *.java
)
java -classpath .;webserve.jar WebServer