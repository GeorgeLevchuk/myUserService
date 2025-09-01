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

Приложение включает комплексную систему обработки исключений для обеспечения стабильной работы и понятных сообщений об ошибках.

#### ✅ Hibernate исключения

##### `ConstraintViolationException`
- **Нарушение ограничений базы данных**
- Уникальность email адресов
- Ограничения целостности данных
- **Пример**: Попытка создать пользователя с уже существующим email

##### `SQLGrammarException`
- **Синтаксические ошибки SQL запросов**
- Неправильные имена таблиц или колонок
- Ошибки в HQL/Hibernate Query Language
- **Пример**: Неправильно составленный запрос к базе данных

##### `DataException`
- **Ошибки формата данных**
- Несоответствие типов данных
- Превышение максимальной длины полей
- **Пример**: Попытка сохранить слишком длинное имя пользователя

##### `ServiceException`
- **Ошибки сервисов Hibernate**
- Проблемы с пулом соединений
- Ошибки конфигурации Hibernate
- **Пример**: Неправильная настройка hibernate.cfg.xml

#### ✅ PostgreSQL исключения

##### **Ошибки подключения к БД**
- Недоступность сервера PostgreSQL
- Неправильные параметры подключения
- **Сообщение**: "Cannot connect to database. Please check: ..."

##### **Ошибки аутентификации**
- Неверное имя пользователя или пароль
- Отсутствие прав доступа к базе данных
- **Сообщение**: "Authentication failed for user"

##### **Ошибки времени выполнения запросов**
- Таймауты соединения
- Перегрузка базы данных
- Проблемы с сетью
- **Сообщение**: "Database operation timeout"

#### ✅ Бизнес-логика исключения

##### `ValidationException`
- **Ошибки валидации данных**
- Пустые или некорректные поля
- Неправильный формат email
- Недопустимые значения возраста
- **Пример**: "Age must be between 1 and 150"

##### `EntityNotFoundException`
- **Сущность не найдена**
- Попытка обновить/удалить несуществующего пользователя
- **Пример**: "User not found with ID: 123"

##### `DatabaseConnectionException`
- **Проблемы с подключением к БД**
- Детальная диагностика проблем подключения
- **Сообщение**: Указание конкретных причин и способов решения

#### 🛡️ Механизмы обработки исключений

1. **Транзакционность**: Автоматический откат транзакций при ошибках
2. **Логирование**: Детальное логирование всех исключений в файл error.log
3. **Пользовательские сообщения**: Понятные сообщения об ошибках для конечного пользователя
4. **Грациозное завершение**: Корректное закрытие ресурсов при любых ошибках
5. **Валидация входных данных**: Предварительная проверка данных до сохранения в БД

#### 📋 Примеры обработки ошибок

```bash
# Ошибка валидации
❌ Validation error: Age must be between 1 and 150

# Ошибка уникальности email
❌ Database error: User with email 'john@example.com' already exists

# Ошибка подключения к БД
❌ CRITICAL: Database Connection Failed
Please check:
1. 📋 Is PostgreSQL running?
2. 🗄️ Is database 'userdb' created?
3. 🔧 Are connection settings correct in hibernate.cfg.xml?
4. 👤 Are username and password correct?
```

### 🔧 Диагностика проблем

При возникновении ошибок проверьте:
1. **Логи приложения**: `logs/user-service.log`
2. **Логи ошибок**: `logs/error.log` (содержит stack traces)
3. **SQL запросы**: Логируются в консоль для отладки
4. **Подключение к БД**: Используйте опцию "Test Database Connection" в меню

Теперь приложение полностью обрабатывает все возможные исключения и предоставляет понятные сообщения об ошибках пользователю, обеспечивая стабильную работу даже в случае сбоев.

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

## 🧪 Тестирование
Приложение включает комплексную систему тестирования с использованием современных инструментов и подходов.

### 📊 Типы тестов
#### 🧩 Unit-тесты (Модульные тесты)
- Назначение: Тестирование отдельных компонентов в изоляции
- Технологии: JUnit 5 + Mockito
- Объекты тестирования: Сервисный слой (UserService)
- Особенности: Mocking зависимостей, быстрые тесты

#### 🔗 Integration-тесты (Интеграционные тесты)
- Назначение: Тестирование взаимодействия с реальной базой данных
- Технологии: JUnit 5 + Testcontainers
- Объекты тестирования: DAO слой (UserDao)
- Особенности: Реальная PostgreSQL в Docker контейнере

### 🛠 Технологии тестирования
- JUnit 5 - фреймворк для написания и запуска тестов
-Mockito - мокирование зависимостей для unit-тестов
-Testcontainers - запуск PostgreSQL в Docker для интеграционных тестов
-Hamcrest - библиотека matchers для улучшенных assertions

## 📁 Структура тестов
```
src/test/java/
└── org/
   └─── example/
         ├─── dao/
         │   ├── BaseDaoTest.java          # Базовый класс для DAO тестов
         │   └── UserDaoIntegrationTest.java # Интеграционные тесты DAO
         └─── service/
               ├────── UserServiceTest.java      # Unit-тесты сервиса
               └────── UserServiceValidationTest.java # Тесты валидации
```

## 🚀 Запуск тестов
### 1. Предварительные требования
#### Убедитесь, что установлены:

- Docker Desktop (для Testcontainers)
- Maven 3.6+
- Java 11+

### 2. Команды для запуска тестов
```bash
# Запуск ВСЕХ тестов (unit + integration)
mvn test


# Запуск только UNIT-тестов (быстрые тесты без Docker)
mvn test -Dtest="*Test" -DfailIfNoTests=false

# Запуск только INTEGRATION-тестов (требует Docker)
mvn test -Dtest="*IntegrationTest" -DfailIfNoTests=false

# Запуск тестов с подробным выводом
mvn test -Dmaven.test.failure.ignore=true

# Запуск конкретного тестового класса
mvn test -Dtest="UserServiceTest"
mvn test -Dtest="UserDaoIntegrationTest"

# Запуск тестов с генерацией отчета покрытия
mvn test jacoco:report
```

### 3. Запуск в IDE (IntelliJ IDEA/Eclipse)
- Откройте проект в IDE
- Убедитесь, что Docker запущен
- Запустите тесты через контекстное меню: 
- 1. Правый клик на test/java → Run Tests
- 2. Или правый клик на конкретном тестовом классе

## 📋 Покрытие тестами
### Unit-тесты покрывают:
- ✅ Валидацию входных данных
- ✅ Бизнес-логику сервисов
- ✅ Обработку исключений
- ✅ Граничные случаи
- ✅ Mocking зависимостей

### Интеграционные тесты покрывают:
- ✅ CRUD операции с реальной БД
- ✅ Транзакционность
- ✅ Ограничения базы данных (unique constraints)
- ✅ Работу Hibernate с PostgreSQL
- ✅ Откат транзакций при ошибках

## 🧪 Примеры тестовых сценариев
Unit-тесты (UserServiceTest):
```java
@Test
void shouldCreateUserSuccessfully() {
// Mock dependencies, test business logic
}

@Test
void shouldThrowValidationExceptionForInvalidEmail() {
// Test validation rules
}
```
Интеграционные тесты (UserDaoIntegrationTest):
```java
@Test
void shouldSaveUserSuccessfully() {
// Real database operations with Testcontainers
}

@Test
void shouldThrowExceptionForDuplicateEmail() {
// Test database constraints
}
```
## ⚙️ Конфигурация Testcontainers
Testcontainers автоматически:

- 🐳 Запускает PostgreSQL в Docker контейнере
- 🔄 Создает временную базу данных для тестов
- 🧹 Очищает данные между тестами
- ⚡ Использует быстрые alpine образы

## 📊 Отчеты о покрытии
Для генерации отчетов о покрытии кода:

```bash
# Установите jacoco plugin в pom.xml
# Запустите тесты с генерацией отчета
mvn test jacoco:report

# Отчет будет доступен по пути:
# target/site/jacoco/index.html
```
## 🐛 Решение проблем
Если тесты не запускаются:
1. Проверьте Docker: Убедитесь, что Docker Desktop запущен
2. Права доступа: На Linux может потребоваться sudo usermod -aG docker $USER
3. Память Docker: Убедитесь, что у Docker достаточно памяти (минимум 2GB)

Если integration-тесты падают:
```bash
# Запустите только unit-тесты для проверки
mvn test -Dtest=!**/UserDaoIntegrationTest -DfailIfNoTests=false

# Проверьте логи Docker
docker logs <container_id>
```
Для отладки тестов:
```bash
# Запуск с debug выводом
mvn test -Dtest="UserServiceTest" -Dmaven.surefire.debug

# Запуск с подробным логированием
mvn test -Dorg.slf4j.simpleLogger.log.org.testcontainers=debug
```

## 📝 Best Practices
1. Изоляция тестов: Каждый тест независим и самодостаточен
2. Чистые данные: База очищается перед каждым тестом
3. Meaningful names: Тесты имеют понятные названия
4. Assertions: Использование Hamcrest для читаемых проверок
5. Mocking: Только необходимые зависимости мокаются

## 🎯 Результаты тестирования
После успешного запуска вы увидите:

```text
[INFO] Results:
[INFO]
[INFO] Tests run: 25, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD SUCCESS
Тесты обеспечивают высокое качество кода и уверенность в работоспособности приложения в различных сценариях.
```
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