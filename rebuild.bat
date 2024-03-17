@echo off
call preprocess.bat -DNDEBUG
if %ERRORLEVEL% EQU 0 (
REM set "JAVA_HOME=E:/java/graalvm-ce-java17-22.1.0"
set "JAVA_HOME=C:/Program Files/Java/jre1.8.0_191"
gradlew.bat build --rerun-tasks
)