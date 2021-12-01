@ECHO OFF

del /q class\
if NOT exist class MKDIR class
if NOT exist SavedLogs MKDIR SavedLogs

javac src/**.java -d class -Xlint:unchecked

IF ["%ERRORLEVEL%"] == ["0"] (
	rem java -cp class; App <maze file> <number of generations> <population size> <mutation rate>
	java -cp class; src.App maze 0 15 60 
)
