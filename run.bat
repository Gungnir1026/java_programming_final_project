@echo off
setlocal
cd /d "%~dp0"
call mvnw.cmd javafx:run
if errorlevel 1 (
    echo.
    echo Launch failed. Please check that JDK 21 or newer is installed.
    pause
    exit /b 1
)
