@echo off
REM Build script yang menggunakan JDK 17 jika tersedia

SET SCRIPT_DIR=%~dp0
SET JDK17_PATH=%SCRIPT_DIR%jdk-17

IF EXIST "%JDK17_PATH%" (
    echo Using JDK 17 from: %JDK17_PATH%
    SET JAVA_HOME=%JDK17_PATH%
    SET PATH=%JDK17_PATH%\bin;%PATH%
    
    echo.
    echo Java Version:
    "%JDK17_PATH%\bin\java.exe" -version
    
    echo.
    echo Building project...
    "%SCRIPT_DIR%gradlew.bat" %*
) ELSE (
    echo JDK 17 not found!
    echo Please run: setup-jdk17.ps1
    echo Or install JDK 17 manually to: %JDK17_PATH%
    pause
)
