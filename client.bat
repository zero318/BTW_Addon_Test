@echo off
call preprocess.bat
if %ERRORLEVEL% EQU 0 (
set "JAVA_HOME=C:/Program Files/Java/jre1.8.0_191"
REM set "JAVA_HOME=F:/My Programs Expansion/Java/jdk-17.0.5_windows-x64_bin/jdk-17.0.5"
REM "%JAVA_HOME%/bin/java" -XX:+UnlockDiagnosticVMOptions -XX:+PrintFlagsFinal -version>java_flags.txt
::set JDK_JAVA_OPTIONS 
call gradlew.bat runClient
)
build_stats_manager.bat 1 %ERRORLEVEL%
exit /b %ERRORLEVEL%