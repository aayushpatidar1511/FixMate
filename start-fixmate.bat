@echo off
setlocal enabledelayedexpansion

echo =====================================================================
echo           FixMate - Launching Platform Application
echo =====================================================================
echo.

set MAVEN_CMD="C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd"
if not exist %MAVEN_CMD% (
    set MAVEN_CMD=mvn
)

set /p DB_PASS="Enter your MySQL root password (press Enter if already configured): "
if not "!DB_PASS!"=="" (
    set DB_PASSWORD=!DB_PASS!
)

echo.
echo Starting FixMate Spring Boot Backend on http://localhost:8080...
echo (Once started, open http://localhost:8080 in your browser)
echo.

start "" http://localhost:8080

cd backend
%MAVEN_CMD% spring-boot:run
pause
