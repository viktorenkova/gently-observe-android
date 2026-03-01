# Инструкции по сборке APK

## Требования

Для сборки APK файла вам понадобится:

1. **Java JDK 8** или выше
   - Скачать: https://adoptium.net/
   - Установить переменную окружения `JAVA_HOME`

2. **Android SDK** (один из вариантов):
   - **Вариант А**: Установить Android Studio (рекомендуется)
     - Скачать: https://developer.android.com/studio
   - **Вариант Б**: Android SDK Command Line Tools
     - Скачать: https://developer.android.com/studio#command-tools

3. **Переменные окружения**:
   ```
   ANDROID_HOME=C:\Users\%USERNAME%\AppData\Local\Android\Sdk
   JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-8...
   ```

## Способ 1: Сборка через PowerShell (рекомендуется)

1. Откройте PowerShell в папке проекта
2. Выполните команду:
   ```powershell
   .\setup-and-build.ps1
   ```

Скрипт автоматически:
- Скачает Gradle Wrapper
- Соберёт Debug APK
- Покажет путь к готовому файлу

## Способ 2: Сборка через Android Studio

1. Откройте проект в Android Studio
2. Дождитесь завершения синхронизации Gradle
3. Выберите: `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
4. Готовый APK будет в: `app/build/outputs/apk/debug/`

## Способ 3: Сборка через командную строку (с Gradle)

Если Gradle установлен глобально:
```bash
gradle assembleDebug
```

Или используйте Gradle Wrapper (после первого запуска setup-and-build.ps1):
```bash
.\gradlew.bat assembleDebug
```

## Установка APK на устройство

### Через ADB:
```bash
adb install app/build/outputs/apk/debug/GentleObserver-v1.0-debug.apk
```

### Вручную:
1. Скопируйте APK на Android устройство
2. На устройстве откройте файл
3. Разрешите установку из неизвестных источников (если спросит)
4. Установите приложение

## Структура проекта

```
m.app/
├── app/
│   ├── build.gradle          # Конфигурация модуля приложения
│   ├── src/
│   │   └── main/
│   │       ├── AndroidManifest.xml
│   │       ├── java/         # Исходный код Java
│   │       └── res/          # Ресурсы (layouts, strings, etc.)
│   └── build/                # Папка сборки (создаётся автоматически)
├── build.gradle              # Корневой build.gradle
├── settings.gradle           # Настройки проекта
├── gradle/
│   └── wrapper/              # Gradle Wrapper
└── BUILD_INSTRUCTIONS.md     # Этот файл
```

## Возможные проблемы

### "ANDROID_HOME is not set"
- Установите переменную окружения ANDROID_HOME
- Перезапустите PowerShell/Command Prompt

### "JAVA_HOME is not set"
- Установите JDK и переменную JAVA_HOME
- Проверьте: `java -version` должно показать версию

### "Could not find gradle-wrapper.jar"
- Запустите `setup-and-build.ps1` для автоматической загрузки
- Или скачайте вручную с https://services.gradle.org/distributions/

### Ошибки компиляции
- Убедитесь, что Android SDK Platform 28 установлен
- В Android Studio: SDK Manager → SDK Platforms → Android 9.0 (API 28)

## Минимальные требования устройства

- Android 4.0 (API 14) и выше
- Экран от 3.5 дюймов
- ~10 МБ свободного места

## Поддержка

При возникновении проблем:
1. Проверьте, что все переменные окружения установлены
2. Попробуйте открыть проект в Android Studio
3. Проверьте логи сборки в папке `app/build/outputs/logs/`
