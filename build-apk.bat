@echo off
chcp 65001 >nul
echo ==========================================
echo  Gentle Observer - APK Builder
echo ==========================================
echo.

:: Check if Android SDK is installed
if "%ANDROID_HOME%"=="" (
    echo [ОШИБКА] Переменная окружения ANDROID_HOME не установлена!
    echo.
    echo Для сборки APK необходимо:
    echo 1. Установить Android Studio или Android SDK Command Line Tools
    echo 2. Установить переменную окружения ANDROID_HOME
    echo.
    echo Пример: set ANDROID_HOME=C:\Users\%USERNAME%\AppData\Local\Android\Sdk
    pause
    exit /b 1
)

echo [OK] Android SDK найден: %ANDROID_HOME%
echo.

:: Check for gradle wrapper
if not exist "gradlew.bat" (
    echo [INFO] Gradle Wrapper не найден. Скачивание...
    call download-gradle-wrapper.bat
    if errorlevel 1 (
        echo [ОШИБКА] Не удалось скачать Gradle Wrapper
        pause
        exit /b 1
    )
)

echo [OK] Gradle Wrapper найден
echo.
echo ==========================================
echo  Начинаем сборку APK...
echo ==========================================
echo.

:: Build debug APK
call gradlew.bat assembleDebug

if errorlevel 1 (
    echo.
    echo [ОШИБКА] Сборка завершилась с ошибкой!
    pause
    exit /b 1
)

echo.
echo ==========================================
echo  СБОРКА УСПЕШНО ЗАВЕРШЕНА!
echo ==========================================
echo.
echo APK файл находится по пути:
echo   app\build\outputs\apk\debug\GentleObserver-v1.0-debug.apk
echo.
echo Для установки на устройство:
echo   adb install app\build\outputs\apk\debug\GentleObserver-v1.0-debug.apk
echo.
pause
