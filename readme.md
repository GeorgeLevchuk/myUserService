# User Service Application

Консольное приложение для управления пользователями с использованием Hibernate и PostgreSQL.

## 📋 Описание

Приложение предоставляет RESTful-like консольный интерфейс для выполнения базовых CRUD операций над сущностью User:
- Создание пользователей
- Просмотр пользователей
- Обновление пользователей
- Удаление пользователей
- Поиск пользователей по различным критериям

## 🛠 Технологии

- **Java 11**
- **Hibernate 5.6.15.Final** (ORM)
- **PostgreSQL** (база данных)
- **Maven** (управление зависимостями)
- **Log4j2** (логирование)
- **DAO паттерн** (архитектура)

## 📦 Структура проекта

```
user-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── userservice/
│   │   │               ├── Main.java                 # Главный класс приложения
│   │   │               ├── dao/
│   │   │               │   ├── UserDao.java          # Интерфейс DAO
│   │   │               │   └── UserDaoImpl.java      # Реализация DAO
│   │   │               ├── model/
│   │   │               │   └── User.java             # Сущность User
│   │   │               ├── util/
│   │   │               │   └── HibernateUtil.java    # Утилита Hibernate
│   │   │               └── exception/
│   │   │                   └── UserException.java    # Кастомные исключения
│   │   └── resources/
│   │       ├── hibernate.cfg.xml                     # Конфигурация Hibernate
│   │       └── log4j2.xml                           # Конфигурация логирования
│   └── test/
│       └── java/
└── pom.xml                                          # Конфигурация Maven
```

## 🚀 Инструкция по запуску

### 1. Установка и настройка PostgreSQL

Установите PostgreSQL и создайте базу данных:

```sql
CREATE DATABASE userdb;
```

### 2. Настройка подключения к базе данных

Обновите настройки подключения в файле `src/main/resources/hibernate.cfg.xml`:

```xml
<property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/userdb</property>
<property name="hibernate.connection.username">your_username</property>
<property name="hibernate.connection.password">your_password</property>
```

### 3. Сборка и запуск приложения

#### Вариант 1: Использование Maven Exec Plugin
```bash
# Скомпилировать проект
mvn clean compile

# Запустить приложение
mvn exec:java
```

#### Вариант 2: Создание JAR файла
```bash
# Создать исполняемый JAR
mvn clean package

# Запустить приложение
java -jar target/user-service-1.0-SNAPSHOT.jar
```

#### Вариант 3: Явное указание главного класса
```bash
mvn exec:java -Dexec.mainClass="com.example.userservice.Main" -Dexec.classpathScope=compile
```

## 📖 Использование

После запуска приложения доступно меню с операциями:

```
=== User Service Menu ===
1. Create User
2. Find User by ID
3. Find All Users
4. Update User
5. Delete User
6. Find User by Email
7. Find Users by Name
8. Exit
```

### Примеры операций:

1. **Создание пользователя**:
    - Введите имя, email и возраст
    - Пользователь автоматически получает ID и timestamp создания

2. **Поиск пользователя**:
    - По ID, email или имени
    - Поддерживает поиск по частичному совпадению имени

3. **Обновление пользователя**:
    - Изменение имени, email или возраста
    - Поле created_at защищено от изменений

4. **Удаление пользователя**:
    - Удаление по ID с подтверждением

## ⚙️ Конфигурация

### Hibernate Configuration (`hibernate.cfg.xml`)
- Автоматическое создание/обновление схемы (hbm2ddl.auto = update)
- Логирование SQL запросов в консоль
- Размер пула соединений: 10
- Диалект: PostgreSQL

### Логирование (`log4j2.xml`)
- Логи выводятся в консоль и файл `logs/user-service.log`
- Уровень логирования: INFO для приложения, WARN для Hibernate

## 🎯 Особенности реализации

### ✅ DAO Паттерн
- Полное разделение логики данных и бизнес-логики
- Интерфейс `UserDao` и реализация `UserDaoImpl`
- Легкость тестирования и замены реализации

### ✅ Транзакционность
- Каждая операция выполняется в отдельной транзакции
- Автоматический откат при ошибках
- Правильное управление сессиями Hibernate

### ✅ Обработка исключений
- Кастомные исключения `UserException`
- Детальное логирование ошибок
- Информативные сообщения для пользователя

### ✅ Логирование
- Подробное логирование всех операций
- Логи в консоль и файл
- Раздельные уровни логирования для приложения и Hibernate

### ✅ Валидация данных
- Проверка входных данных
- Обработка некорректного ввода
- Защита от SQL инъекций через параметризованные запросы

### ✅ Управление ресурсами
- Автоматическое закрытие ресурсов (try-with-resources)
- Правильное управление сессиями и транзакциями
- Гарантированное освобождение ресурсов

### ✅ Безопасность
- Подготовленные statements для защиты от SQL инъекций
- Валидация входных данных
- Обработка исключений на всех уровнях

## 🐛 Отладка и логи

При возникновении ошибок проверьте:
1. **Логи приложения**: `logs/user-service.log`
2. **SQL запросы**: выводятся в консоль (настроено в hibernate.cfg.xml)
3. **Подключение к БД**: убедитесь, что PostgreSQL запущен и доступен

## 📝 Пример работы

```bash
# Запуск приложения
mvn exec:java

# В консоли:
=== User Service Menu ===
1. Create User
2. Find User by ID
3. Find All Users
4. Update User
5. Delete User
6. Find User by Email
7. Find Users by Name
8. Exit
Choose an option: 1

--- Create User ---
Enter name: John Doe
Enter email: john@example.com
Enter age: 30
User created successfully: User{id=1, name='John Doe', email='john@example.com', age=30, createdAt=2024-01-15T10:30:45.123}
```

## 🔧 Возможные проблемы и решения

1. **Ошибка подключения к PostgreSQL**:
    - Проверьте, что PostgreSQL запущен
    - Убедитесь в правильности username/password в hibernate.cfg.xml
    - Проверьте, что база данных `userdb` существует

2. **Файл hibernate.cfg.xml не находится**:
    - Убедитесь, что файл находится в `src/main/resources/`
    - Выполните `mvn clean compile` для пересборки

3. **Ошибки компиляции**:
    - Проверьте версию Java (требуется 11+)
    - Убедитесь, что все зависимости загружены (`mvn clean compile`)

## 📄 Лицензия

Это учебный проект, разработанный для демонстрации использования Hibernate с PostgreSQL.

## 🤝 Вклад

Для внесения изменений:
1. Форкните репозиторий
2. Создайте feature branch
3. Commit ваши изменения
4. Push в branch
5. Создайте Pull Request

---

**Примечание**: Это консольное приложение предназначено для демонстрационных и учебных целей. В production среде рекомендуется использовать веб-интерфейс и дополнительные механизмы безопасности.