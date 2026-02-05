# Поздравлятор

Тестовое задание - Уровень 5

Веб-приложение для учета ДР с автоматической рассылкой уведомлений в Telegram.

## Технологии

- **Backend**: Java 17, Spring Boot 3.5, Spring Data JPA, PostgreSQL
- **Frontend**: Vanilla JavaScript (SPA), HTML, CSS
- **Уведомления**: Telegram Bot API
- **Планировщик**: Spring Scheduler

## Функциональность

- Управление списком дней рождения (CRUD)
- Загрузка и отображение фотографий
- Главная страница с ближайшими ДР (30 дней)
- Автоматическая рассылка уведомлений в Telegram по расписанию
- Расчет возраста и дней до ДР


### 1. Конфигурация рассылки в Telegram
Скопируйте файл `src/main/resources/application-secrets.properties.example` в `application-secrets.properties`

Заполните своими данными:
```properties
telegram.bot.token=YOUR_BOT_TOKEN_HERE
telegram.bot.chat-ids=YOUR_CHAT_ID_HERE
```

### 2. Настройка расписания
В `application.properties` измените расписание (по умолчанию - каждую минуту):
```properties
# Каждый день в 9:00
birthday.notification.cron=0 0 9 * * *
```

### 3. Запуск
```bash
./gradlew bootRun
```

Приложение доступно по адресу: **http://localhost:8081**

## API Endpoints

- `GET /api/persons` - все записи
- `GET /api/persons/next` - ближайшие ДР (30 дней)
- `GET /api/persons/{id}` - запись по ID
- `POST /api/persons` - создать запись
- `PUT /api/persons/{id}` - обновить запись
- `DELETE /api/persons/{id}` - удалить запись
- `POST /api/photos/{personId}` - загрузить фото
- `GET /api/photos/{personId}` - получить фото

## Структура проекта

```
src/
├── main/
│   ├── java/.../birthday/
│   │   ├── config/         # Конфигурация Telegram Bot
│   │   ├── controller/     # REST контроллеры
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # JPA сущности
│   │   ├── mapper/         # MapStruct маппер
│   │   ├── repository/     # Spring Data репозитории
│   │   ├── scheduler/      # Планировщик уведомлений
│   │   └── service/        # Бизнес-логика
│   └── resources/
│       ├── static/         # Frontend (HTML, CSS, JS)
│       └── application.properties
```



