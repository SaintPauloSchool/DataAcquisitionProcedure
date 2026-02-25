@echo off
echo Starting CSV Import Program...

cd /d "%~dp0"

java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Java not found. Please install Java 11+
    pause
    exit /b 1
)

if not exist "target\DataAcquisitionProcedure-0.0.1-SNAPSHOT.jar" (
    echo Program file not found. Run: mvn clean package -DskipTests
    pause
    exit /b 1
)

echo Configuration:
echo CSV Path: C:\OutPut
echo Schedule: Mon-Fri 7:50 PM
echo Database: localhost:3306/student_handbook
echo.

echo Starting program... Press Ctrl+C to stop
echo ========================================

java -jar target/DataAcquisitionProcedure-0.0.1-SNAPSHOT.jar

pause