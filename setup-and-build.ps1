# Gentle Observer - Setup and Build Script
# Run this script in PowerShell to download Gradle wrapper and build APK

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  Gentle Observer - Setup and Build" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# Check Android SDK
$androidHome = $env:ANDROID_HOME
if (-not $androidHome) {
    Write-Host "[ERROR] ANDROID_HOME environment variable is not set!" -ForegroundColor Red
    Write-Host ""
    Write-Host "To build APK you need:"
    Write-Host "1. Install Android Studio or Android SDK Command Line Tools"
    Write-Host "2. Set ANDROID_HOME environment variable"
    Write-Host ""
    Write-Host "Example: [Environment]::SetEnvironmentVariable('ANDROID_HOME', 'C:\Users\$env:USERNAME\AppData\Local\Android\Sdk', 'User')"
    exit 1
}

Write-Host "[OK] Android SDK found: $androidHome" -ForegroundColor Green
Write-Host ""

# Download Gradle Wrapper if not exists
$gradleWrapperJar = "gradle/wrapper/gradle-wrapper.jar"
if (-not (Test-Path $gradleWrapperJar)) {
    Write-Host "[INFO] Downloading Gradle Wrapper..." -ForegroundColor Yellow
    
    $wrapperUrl = "https://raw.githubusercontent.com/gradle/gradle/v5.4.1/gradle/wrapper/gradle-wrapper.jar"
    
    try {
        Invoke-WebRequest -Uri $wrapperUrl -OutFile $gradleWrapperJar -ErrorAction Stop
        Write-Host "[OK] Gradle Wrapper downloaded" -ForegroundColor Green
    } catch {
        Write-Host "[ERROR] Failed to download Gradle Wrapper" -ForegroundColor Red
        Write-Host "Please download manually from: https://services.gradle.org/distributions/gradle-5.4.1-bin.zip"
        exit 1
    }
} else {
    Write-Host "[OK] Gradle Wrapper exists" -ForegroundColor Green
}

# Download gradlew.bat if not exists
$gradlewBat = "gradlew.bat"
if (-not (Test-Path $gradlewBat)) {
    Write-Host "[INFO] Downloading gradlew.bat..." -ForegroundColor Yellow
    
    $gradlewContent = @'
@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  Gradle startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar


@rem Execute Gradle
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable GRADLE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%GRADLE_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
'@
    
    Set-Content -Path $gradlewBat -Value $gradlewContent
    Write-Host "[OK] gradlew.bat created" -ForegroundColor Green
} else {
    Write-Host "[OK] gradlew.bat exists" -ForegroundColor Green
}

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  Building APK..." -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# Build APK
& .\gradlew.bat assembleDebug

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host "  BUILD SUCCESSFUL!" -ForegroundColor Green
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "APK location:"
    Write-Host "  app\build\outputs\apk\debug\GentleObserver-v1.0-debug.apk" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "To install on device:"
    Write-Host "  adb install app\build\outputs\apk\debug\GentleObserver-v1.0-debug.apk" -ForegroundColor Yellow
    Write-Host ""
} else {
    Write-Host ""
    Write-Host "[ERROR] Build failed!" -ForegroundColor Red
    exit 1
}
