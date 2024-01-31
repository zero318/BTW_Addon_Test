@echo off
setlocal enabledelayedexpansion
xcopy /Y ".\base_gradle_properties.txt" ".\gradle.properties"
for /f usebackq %%G in ("_VERSION.txt") do set $ModVersion=%%G
echo mod_version = !$ModVersion!>>gradle.properties
echo #define MOD_VERSION !$ModVersion!>src\main\javac\zero\test\_VERSION.h
exit /b