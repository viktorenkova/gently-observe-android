# Хранение данных в приложении Gentle Observer

## Где сохраняются ответы пользователя

### 1. Локальная база данных SQLite

Все ответы пользователя сохраняются в **локальной базе данных SQLite** на устройстве:

```
Путь: /data/data/com.gentleobserver.app/databases/gentle_observer.db
```

### 2. Структура хранения

**Таблица: `checkins`**

Каждый чек-ин сохраняется как одна запись со следующими полями:

| Поле | Тип | Описание |
|------|-----|----------|
| `id` | INTEGER | Уникальный ID записи |
| `type` | TEXT | Тип чек-ина: "daytime" или "evening" |
| `timestamp` | INTEGER | Время создания (Unix timestamp) |
| `completed_at` | INTEGER | Время завершения |
| `skipped` | INTEGER | Был ли пропущен (0/1) |

**Блок Body (тело):**
| `hunger` | TEXT | Голод: "yes", "somewhat", "no" |
| `fatigue` | TEXT | Усталость: "yes", "no" |
| `tension` | TEXT | Напряжение: "yes", "no" |

**Блок Food (еда):**
| `hours_since_meal` | TEXT | >4 часов: "yes", "no", "unsure" |
| `ate_since_last` | TEXT | Ели с прошлого чек-ина: "yes", "no" |
| `food_enjoyment` | TEXT | Приятность еды: "yes", "okay", "not_really" |

**Блок Mindfulness (осознанность):**
| `ate_mindfully` | TEXT | Ели осознанно: "yes", "partially", "no" |
| `tasted_food` | TEXT | Чувствовали вкус: "yes", "somewhat", "no" |

**Блок Emotions (эмоции):**
| `mood` | TEXT | Настроение: "calm", "tired", "irritated", "sad" |
| `urge_to_eat` | TEXT | Желание заесть: "yes", "no" |

**Вечерний чек-ин:**
| `overeating_episodes` | TEXT | Эпизоды переедания: "yes", "no", "unsure" |
| `long_gaps` | TEXT | Перерывы >5-6 часов: "yes", "no", "dont_remember" |
| `day_overall` | TEXT | День в целом: "calm", "hard", "exhausting" |
| `current_hunger` | TEXT | Текущий голод: "yes", "somewhat", "no" |

**Expressive Window:**
| `expressive_content` | TEXT | Текст/путь к аудио |
| `expressive_content_type` | TEXT | Тип: "text" или "voice" |
| `expressive_block_type` | TEXT | К какому блоку относится |

### 3. Пример записи в базе данных

```sql
INSERT INTO checkins (
    type, timestamp, hunger, fatigue, tension, 
    hours_since_meal, ate_since_last, food_enjoyment,
    ate_mindfully, tasted_food, mood, urge_to_eat
) VALUES (
    'daytime', 1709145600000, 'somewhat', 'yes', 'no',
    'yes', 'yes', 'okay',
    'partially', 'yes', 'tired', 'no'
);
```

### 4. Как происходит сохранение

**В коде:**
```java
// В CheckInActivity.java
CheckIn checkIn = new CheckIn();
checkIn.setHunger("somewhat");
checkIn.setFatigue("yes");
// ... другие поля

// Сохранение в базу
checkIn.markCompleted();
dbHelper.insertCheckIn(checkIn);
```

**Класс DatabaseHelper:**
- Создаёт таблицу при первом запуске
- Сохраняет данные через `insertCheckIn()`
- Читает данные через `getAllCheckIns()`, `getCheckInsForRange()`
- Удаляет данные через `deleteAllCheckIns()`

### 5. Где данные НЕ сохраняются

❌ **Данные не отправляются на сервер** — всё хранится только на устройстве  
❌ **Нет облачной синхронизации** — никаких внешних сервисов  
❌ **Нет аналитики** — данные не передаются третьим лицам  

### 6. Резервное копирование

Данные можно выгрузить:
- Через `Settings` → `Экспорт всех данных`
- Форматы: CSV или PDF
- Данные отправляются по выбору пользователя (email, мессенджеры)

### 7. Удаление данных

Пользователь может удалить все данные:
- `Settings` → `Удалить все данные`
- Или удалить приложение — база данных удалится вместе с ним

### 8. Доступ к данным

**Только пользователь может:**
- Просматривать свою историю (HistoryActivity)
- Видеть аналитику (AnalyticsActivity)
- Экспортировать данные
- Удалять данные

**Приложение НЕ:**
- Не анализирует данные автоматически
- Не даёт рекомендаций на основе данных
- Не отправляет данные врачу автоматически

### 9. Безопасность

- Данные хранятся в приватной директории приложения
- Доступ только у приложения (Android security sandbox)
- На рутированных устройствах данные теоретически доступны
- Нет шифрования базы данных (можно добавить при необходимости)

### 10. Размер данных

Примерный размер одного чек-ина: ~1-2 KB
- Текстовые expressive notes добавляют больше
- Аудио заметки хранятся отдельно как файлы

За месяц использования (~90 чек-инов): ~100-200 KB
