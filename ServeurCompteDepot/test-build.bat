@echo off
echo Testing ServeurCompteDepot Build...
cd "c:\Users\Fetraniaina\OneDrive\Documents\S5\INF301 - Archi log - Mr Tahina\banking system\ServeurCompteDepot"

echo.
echo Building application...
dotnet build

echo.
if %errorlevel% equ 0 (
    echo Build SUCCESSFUL!
    echo.
    echo Running application...
    timeout /t 3 /nobreak >nul
    dotnet run --no-build
) else (
    echo Build FAILED!
    echo Check the error messages above.
)

echo.
echo Press any key to exit...
pause >nul