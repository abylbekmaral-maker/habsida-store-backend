## Store API Project
REST API для управление магазинами, товарами и заказами.

## 🛠 Технологический стек

* **Language**: Java 25 (LTS)
* **Framework**: Spring Boot 4.x (Spring Web, Spring Data JPA, Spring Security)
* **Authentication**: JWT (JSON Web Tokens) + Spring Security
* **Database**: PostgreSQL
* **Migrations**: Flyway
* **API Documentation**: Swagger UI / OpenAPI (springdoc-openapi)
* **Validation**: Jakarta Validation (`@NotNull`, `@NotBlank`, etc.)
* **Logging**: SLF4J + Logback
* **Testing**: JUnit 5 + Spring Boot Test
* **Infrastructure**: Docker & Docker Compose
* 
## Как запустить проект

1. **Поднять базу данных**
    Проверить запущен ли Docker, и выполнить файл в корне проекта:
    'docker-compose up -d'

2. **Запустить приложение**
    При запуске приложения Flyway автоматический загрузит все миграции из 'src/main/resources/db/migration'.
    В среде 'dev' автоматический создаются тестовые данные (админы, магазины, товары).
    './mvnw spring-boot:run -Dspring-boot.run.profiles=dev'

3. **Документация API (Swagger UI):**
    После запуска перейти по ссылке: http://localhost:8080/swagger-ui/index.html

4. **Документация OpenAPI JSON Spec:**
   После запуска перейти по ссылке: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

5. **Аутентификация(JWT)**
   Для выполнения защищенных запросов в Swagger UI:
   Выполните POST /api/auth/login с данными администратора (см. ниже).
   Скопируйте полученный token.
   Нажмите кнопку Authorize в правом верхнем углу Swagger UI.
   Введите токен в формате: Bearer <ваш_токен>

6. **Предзагруженые тестовые данные**
   Администратор:
   Username: admin
   Email: admin@gmail.com
   Password: admin123
 
   Пользователь / Владелец:
   Username: dev
   Email: devUser@gmail.com
   Password: devUser
   Магазин: Fruit Shop (slug: fruit-shop) — привязан к dev.
   Категории: Apples(slug: apples), Citrus(slug: citrus).
   Товары: Antonovka Apple ($5.50), Granny Smith Apple ($6.00).
   Модификаторы: Группа "Type packing" (Plastic Bag - $0.00, Box - $0.50, Bag - $1.00).