@echo off
set "STARTTIME=%time: =0%"
call preprocess.bat -DNDEBUG
if %ERRORLEVEL% EQU 0 (
REM set "JAVA_HOME=E:/java/graalvm-ee-java8-21.3.4"
set "JAVA_HOME=C:/Program Files/Java/jre1.8.0_191"
call gradlew.bat build
)
set "ENDTIME=%time: =0%"
build_stats_manager.bat 0 %ERRORLEVEL% %STARTTIME% %ENDTIME%
exit /b %ERRORLEVEL%