@echo off
setlocal enabledelayedexpansion

echo =====================================================================
echo           FixMate - Automated MySQL Database Setup Script
echo =====================================================================
echo.

set MYSQL_PATH="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
if not exist %MYSQL_PATH% (
    set MYSQL_PATH=mysql
)

set /p DB_PASS="Enter your MySQL root password (e.g. root or admin): "

echo.
echo [1/8] Executing 01_schema.sql (Creating 17 normalized tables)...
%MYSQL_PATH% -u root -p!DB_PASS! < database\01_schema.sql
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Failed to execute 01_schema.sql. Please verify your root password.
    pause
    exit /b %ERRORLEVEL%
)

echo [2/8] Executing 02_seed.sql (Loading Ujjain/Indore/Bhopal realistic data)...
%MYSQL_PATH% -u root -p!DB_PASS! fixmate_db < database\02_seed.sql

echo [3/8] Executing 03_indexes.sql (Applying composite performance indexes)...
%MYSQL_PATH% -u root -p!DB_PASS! fixmate_db < database\03_indexes.sql

echo [4/8] Executing 04_views.sql (Creating analytical reporting views)...
%MYSQL_PATH% -u root -p!DB_PASS! fixmate_db < database\04_views.sql

echo [5/8] Executing 05_procedures.sql (Setting up ACID stored procedures)...
%MYSQL_PATH% -u root -p!DB_PASS! fixmate_db < database\05_procedures.sql

echo [6/8] Executing 06_functions.sql (Haversine spherical distance function)...
%MYSQL_PATH% -u root -p!DB_PASS! fixmate_db < database\06_functions.sql

echo [7/8] Executing 07_triggers.sql (Rating recalculation and slot collision guards)...
%MYSQL_PATH% -u root -p!DB_PASS! fixmate_db < database\07_triggers.sql

echo [8/8] Testing connection...
%MYSQL_PATH% -u root -p!DB_PASS! -e "USE fixmate_db; SELECT 'FixMate Database Initialized Successfully!' AS Status, COUNT(*) AS TotalUsers FROM users;"

echo.
echo =====================================================================
echo  DATABASE INITIALIZATION COMPLETE!
echo  Next step: Double click 'start-fixmate.bat' to run the application!
echo =====================================================================
echo.
pause
