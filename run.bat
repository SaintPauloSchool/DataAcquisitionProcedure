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
echo CSV Path: \\10.96.48.253\Stu_Dailybook$
echo Schedule: Mon-Fri 17:30 PM
echo Database: localhost:3306/school_student_management
echo.

echo Starting program... Press Ctrl+C to stop
echo ========================================

java -jar target/DataAcquisitionProcedure-0.0.1-SNAPSHOT.jar

pause