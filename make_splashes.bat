@echo off
setlocal enabledelayedexpansion
:: Is there really no better way to add custom splashes?
xcopy /Y ".\src\btw\resources\title\splashes.txt" ".\src\main\resources\title\splashes.txt"
echo:>>.\src\main\resources\title\splashes.txt
type custom_splashes.txt>>.\src\main\resources\title\splashes.txt
exit /b