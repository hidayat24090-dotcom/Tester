@echo off
echo ========================================
echo   Sistem Inventaris dan Peminjaman Ruangan
echo ========================================
echo.

REM Set paths
SET JAVAFX_PATH=C:\javafx-sdk-17\lib
SET PROJECT_PATH=C:\Users\Axioo Pongo\Desktop\sistem-ruangan

cd "%PROJECT_PATH%"

REM Check if compiled
if not exist "bin\com\sistemruangan\MainApp.class" (
    echo [ERROR] Project not compiled yet!
    echo [INFO] Please run compile.bat first
    pause
    exit /b 1
)

echo [INFO] Starting application...
echo.

REM Run with resources in classpath
java --module-path "%JAVAFX_PATH%" ^
     --add-modules javafx.controls,javafx.fxml ^
     -cp "bin;lib\*;resources" ^
     com.sistemruangan.MainApp

if errorlevel 1 (
    echo.
    echo [ERROR] Application failed to start!
    pause
    exit /b 1
)

echo.
echo [INFO] Application closed.
pause