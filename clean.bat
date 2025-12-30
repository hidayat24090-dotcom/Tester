@echo off
echo ========================================
echo   CLEAN - Build Files
echo ========================================
echo.

SET PROJECT_PATH=C:\Users\Axioo Pongo\Desktop\sistem-ruangan

cd "%PROJECT_PATH%"

if exist "bin" (
    echo [INFO] Removing bin directory...
    rmdir /s /q bin
    echo [SUCCESS] bin directory removed
) else (
    echo [INFO] bin directory not found
)

echo.
echo [SUCCESS] Clean completed!
echo [INFO] You can now run: compile.bat
echo.
pause