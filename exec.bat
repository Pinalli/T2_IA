@ECHO OFF

del /q class\
if NOT exist class MKDIR class
if NOT exist SavedLogs MKDIR SavedLogs

javac *.java -d class

IF ["%ERRORLEVEL%"] == ["0"] (
	java -cp class; App %1 %2 %3 %4
	rem java -cp class; App maze 100000 20 60
)
