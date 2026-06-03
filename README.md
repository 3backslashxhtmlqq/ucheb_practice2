<img width="1457" height="1573" alt="417-4171156_download-book-stack-icon-free-clipart-library-library-book-icon-transparent-background" src="https://github.com/user-attachments/assets/22f1001c-ee6f-47ab-a591-565dfbd8a75a" />

# Персональный книжный каталог (Google Books API)

Учебный проект на Java Swing для ведения личных книжных каталогов с ролями «Пользователь» и «Администратор» и импортом книг через Google Books API.
# Основные функции
## Пользователь
* Регистрация / авторизация (пароль хэшируется BCrypt)
* Просмотр, добавление, редактирование, удаление книг в личном каталоге
* Поиск и фильтрация по названию/автору
* Импорт книги из Google Books API (по названию/автору)
* Оставление впечатлений (рецензий) и цитат с номерами страниц

## Администратор
* Добавление / удаление / блокировка / разблокировка пользователей
* Назначение роли (USER / ADMIN)
* Просмотр и редактирование каталогов любого пользователя

## Технологии
* Java 17+ 
* Swing (MVC)
* MySQL 8.0
* JDBC + HikariCP
* Google Books API (REST, JSON, Jackson)
* jBCrypt (хэширование паролей), Logback (логирование)

## Требования
* ОС: Windows / Linux / macOS
* Java JRE 17+
* MySQL 8.0 (доступ по сети)
* Интернет (только для импорта через API)

## Быстрая установка
Установите MySQL, создайте базу book_catalog и пользователя.
Выполните скрипт инициализации таблиц (поставляется в init_db.sql).
Настройте подключение к БД в config.properties:
properties:

    db.url=jdbc:mysql://localhost:3306/book_catalog
    db.user=catalog_user
    db.encrypted.password=<зашифрованный пароль AES>

Запустите JAR-файл:

    java -jar book-catalog.jar

При первом запуске создаётся администратор admin / admin (пароль смените).
