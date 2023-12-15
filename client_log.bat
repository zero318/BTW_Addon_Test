@echo off

setlocal

set "GRADLE_OPTS=-Dmixin.debug.export=true"

client.bat >trash_client_output.txt 2>trash_client_errors.txt

endlocal