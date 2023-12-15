@echo off
call preprocess.bat
if %ERRORLEVEL% EQU 0 (
REM set "JAVA_HOME=E:/java/graalvm-ee-java8-21.3.4"
set "JAVA_HOME=C:/Program Files/Java/jre1.8.0_191"
gradlew.bat build
)