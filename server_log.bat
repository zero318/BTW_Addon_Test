@echo off

setlocal

set "GRADLE_OPTS=-Dmixin.debug.export=true"

server.bat >trash_server_output.txt 2>trash_server_errors.txt

endlocal