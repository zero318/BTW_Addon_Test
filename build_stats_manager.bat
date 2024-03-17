@echo off
setlocal enabledelayedexpansion
setlocal
for /f usebackq^ delims^=^=^ tokens^=1^,2 %%G in ("build_stats.txt") do call set "$%%G=%%H"
if "%*"=="" ( set $
) else ( set /a $TotalBuilds+=1
if "%1"=="0" ( set /a $TotalReleases+=1
if "%2"=="0" ( set /a $SuccessReleases+=1
) else ( set /a $FailedReleases+=1
)
) else ( set /a $TotalRuns+=1
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
echo ; Build times tracked as of version 0.2.2
if "%1"=="0" (
:: IDK how this works, I just copied this https://stackoverflow.com/questions/9922498/calculate-time-difference-in-windows-batch-file
set "startTime=%3"
set "endTime=%4"
set "end=!endTime:%time:~8,1%=%%100)*100+1!"  &  set "start=!startTime:%time:~8,1%=%%100)*100+1!"
set /A "elap=((((10!end:%time:~2,1%=%%100)*60+1!%%100)-((((10!start:%time:~2,1%=%%100)*60+1!%%100), elap-=(elap>>31)*24*60*60*100"
set /A "cc=elap%%100+100,elap/=100,ss=elap%%60+100,elap/=60,mm=elap%%60+100,hh=elap/60+100"
echo PrevReleaseBuildTime=!hh:~1!!time:~2,1!!mm:~1!!time:~2,1!!ss:~1!!time:~8,1!!cc:~1!
) else ( echo PrevReleaseBuildTime=!$PrevReleaseBuildTime!
)
)>build_stats.txt
)
endlocal