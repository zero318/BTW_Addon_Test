@echo off
setlocal enabledelayedexpansion
setlocal
for /f usebackq^ delims^=^=^ tokens^=1^,2 %%G in ("build_stats.txt") do call set "$%%G=%%H"
if "%*"=="" (
set $
) else (
set /a $TotalBuilds+=1
if "%1"=="0" (
set /a $TotalReleases+=1
if "%2"=="0" ( set /a $SuccessReleases+=1
) else ( set /a $FailedReleases+=1
)
) else (
set /a $TotalRuns+=1
if "%2"=="0" ( set /a $SuccessRuns+=1
) else ( set /a $FailedRuns+=1
)
)
(
echo ; Stats tracked as of version 0.0.9
echo TotalBuilds=!$TotalBuilds!
echo TotalRuns=!$TotalRuns!
echo SuccessRuns=!$SuccessRuns!
echo FailedRuns=!$FailedRuns!
echo TotalReleases=!$TotalReleases!
echo SuccessReleases=!$SuccessReleases!
echo FailedReleases=!$FailedReleases!
)>build_stats.txt
)
endlocal