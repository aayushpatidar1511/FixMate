@echo off
setlocal enabledelayedexpansion
title FixMate - Cloud MySQL Schema Initializer
color 0b

echo ==============================================================================
echo        FixMate - Remote Cloud MySQL Database Initializer
echo ==============================================================================
echo.
echo This script connects to your remote Cloud MySQL (Aiven, TiDB, Railway, etc.)
echo and automatically executes:
echo   [1] 01_schema.sql      - 17 Relational Tables with 3NF constraints
echo   [2] 02_seed.sql        - 20 Providers, 10 Categories, 14 Services, Slots
echo   [3] 03_indexes.sql     - High-speed B-Tree and Composite Indexes
echo   [4] 04_views.sql       - Materialized and Reporting Views
echo   [5] 05_procedures.sql  - ACID Stored Procedures (Booking, Wallet, Dispute)
echo   [6] 06_functions.sql   - Haversine Geospatial Distance Formula
echo   [7] 07_triggers.sql    - Status Audit History and Notification Triggers
echo.
echo ==============================================================================
echo.

set /p DB_HOST="Enter Cloud MySQL Host (e.g. mysql-xyz.aivencloud.com): "
set /p DB_PORT="Enter Cloud MySQL Port (e.g. 12345 or 3306): "
set /p DB_USER="Enter Cloud MySQL User (e.g. avnadmin or root): "
set /p DB_PASS="Enter Cloud MySQL Password: "
set /p DB_NAME="Enter Database Name (press Enter for defaultdb or fixmate_db): "

if "%DB_NAME%"=="" set DB_NAME=defaultdb

echo.
echo Connecting to %DB_HOST%:%DB_PORT% / %DB_NAME% as %DB_USER%...
echo.

set MYSQL_CMD="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
if not exist %MYSQL_CMD% (
    set MYSQL_CMD=mysql
)

echo [*] Executing 01_schema.sql...
%MYSQL_CMD% -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% %DB_NAME% < "database\01_schema.sql"
if %errorlevel% neq 0 (
    echo [!] Warning or Error during 01_schema.sql execution.
)

echo [*] Executing 02_seed.sql...
%MYSQL_CMD% -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% %DB_NAME% < "database\02_seed.sql"

echo [*] Executing 03_indexes.sql...
%MYSQL_CMD% -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% %DB_NAME% < "database\03_indexes.sql"

echo [*] Executing 04_views.sql...
%MYSQL_CMD% -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% %DB_NAME% < "database\04_views.sql"

echo [*] Executing 05_procedures.sql...
%MYSQL_CMD% -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% %DB_NAME% < "database\05_procedures.sql"

echo [*] Executing 06_functions.sql...
%MYSQL_CMD% -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% %DB_NAME% < "database\06_functions.sql"

echo [*] Executing 07_triggers.sql...
%MYSQL_CMD% -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% %DB_NAME% < "database\07_triggers.sql"

echo.
echo ==============================================================================
echo [*] Cloud Database Initialized Successfully!
echo ==============================================================================
echo.
pause
