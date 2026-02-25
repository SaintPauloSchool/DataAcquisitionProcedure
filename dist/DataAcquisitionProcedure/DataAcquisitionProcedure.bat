@echo off
title Data Acquisition Procedure
cd /d "%~dp0"
echo ========================================
echo Data Acquisition Procedure
echo ========================================
echo Starting application...

REM Check if Java is available
java -version >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo Java runtime detected, starting application...
    java -jar DataAcquisitionProcedure-0.0.1-SNAPSHOT.jar
) else (
    echo Error: Java runtime not found
    echo.
    echo Please install Java 11 or higher from:
    echo https://adoptium.net/
    echo.
    echo Or contact your system administrator.
    echo.
    pause
    exit /b 1
)

pause
