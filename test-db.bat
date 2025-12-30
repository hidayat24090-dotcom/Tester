@echo off
echo ========================================
echo   TEST KONEKSI DATABASE
echo ========================================
echo.

SET JAVAFX_PATH=C:\javafx-sdk-17\lib
SET PROJECT_PATH=C:\Users\Axioo Pongo\Desktop\sistem-ruangan

cd "%PROJECT_PATH%"

if not exist "bin\com\sistemruangan\util\DatabaseConnection.class" (
    echo [INFO] Compiling DatabaseConnection...
    javac -cp "lib\*" -d bin src\com\sistemruangan\util\DatabaseConnection.java
)

echo [INFO] Testing database connection...
echo.

java -cp "bin;lib\*" com.sistemruangan.util.DatabaseConnection

echo.
pause
