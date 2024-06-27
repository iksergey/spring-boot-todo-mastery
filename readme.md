# Прежде чем начать

## Поддержите проект звездочкой на GitHub!

Если вам понравился проект, не забудьте поставить ему звездочку на GitHub. Это простое действие имеет огромное значение для меня.

### Как поставить звездочку?

Просто нажмите на кнопку "Star" в верхнем правом углу репозитория на GitHub. Это займет всего секунду, но принесет нам море позитива и мотивации! 🙏🏼

# Начало проекта

## 1. Настройка проекта

1. Перейдите на сайт Spring Initializr (https://start.spring.io/).
2. Выберите похожие параметры:
Project: `Maven`
Language: `Java`

Spring Boot: `3.3.1`

Group: `ru.ksergey`
Artifact: `todolist`
Name: `todolist`
Description: `ToDo List Project for Spring Boot`
Packaging: `Jar`
Java: `22`

3. Добавьте следующие зависимости:
Dependencies

- MySQL Driver SQL
_MySQL JDBC driver._

- Spring Web WEB
_Build web, including RESTful, applications using Spring MVC. Uses Apache Tomcat as the default embedded container._

- JDBC API SQL
_Database Connectivity API that defines how a client may connect and query a database._

**Вариативно**
- Lombok DEVELOPER TOOLS
_Java annotation library which helps to reduce boilerplate code._
Либо руками через `pom.xml` [projectlombok](https://mvnrepository.com/artifact/org.projectlombok/lombok)

4. Нажмите "Generate" и распакуйте полученный ZIP-файл.

## 2. Настройка базы данных

[Если разворачиваете в Doker](https://t.me/iksergeyru/90) 

```bash
docker run --name mysql-container -p 3306:3306 -e MYSQL_ROOT_PASSWORD=12345678 -d mysql
```

1. Создайте базу данных MySQL:

```sql
DROP DATABASE IF EXISTS todolist_db;
CREATE DATABASE IF NOT EXISTS todolist_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE todolist_db;
```

2. Создайте таблицу

```sql
CREATE TABLE todo_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

3. Наполните, при необходимости
   
```sql
INSERT INTO todo_items (title, description, status, created_at) VALUES
('Завершить проект по Spring Boot', 'Реализовать все основные функции получения одной записи и обновления имеющейся', 'IN_PROGRESS', NOW()),
('Разобраться с REST API', 'Изучить принципы RESTful архитектуры и применить их в проекте', 'IN_PROGRESS', NOW() - INTERVAL 1 DAY),
('Подготовиться к собеседованию', 'Повторить алгоритмы и структуры данных, подготовить вопросы для работодателя', 'PENDING', NOW()),
('Прочитать книгу "Чистый код"', 'Прочитать первые 5 глав и сделать заметки', 'IN_PROGRESS', NOW() - INTERVAL 1 DAY),
('Ознакомиться со Spring Data JPA', 'Разобраться с основами ORM, написать репозитории и сущности', 'PENDING', NOW());
```

4. Выборка всех записей

```sql
SELECT * FROM todo_items;
```

5. Отредактируйте файл `src/main/resources/application.properties`:

```properties
spring.application.name=todolist  
server.servlet.encoding.charset=UTF-8  
server.servlet.encoding.enabled=true  
server.servlet.encoding.force=true 

spring.datasource.url=jdbc:mysql://localhost:3306/todolist_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC  

spring.datasource.username=your_username
spring.datasource.password=your_password
```

## 3. Создание модели

Создайте класс `TodoItem` в пакете `ru.ksergey.todolist.model`:

```java
package ru.ksergey.todolist.model;

import java.time.LocalDateTime;

public class TodoItem {
    private Long id;
    private String title;
    private String description;
    private TodoStatus status;
    private LocalDateTime createdAt;

    public enum TodoStatus {
        PENDING, IN_PROGRESS, COMPLETED
    }

    // Конструкторы, геттеры и сеттеры
}
```

## 4. Создание DAO (Data Access Object)

Создайте интерфейс `TodoItemDao` и его реализацию `TodoItemDaoImpl` в пакете `ru.ksergey.todolist.dao`:

```java
package ru.ksergey.todolist.dao;

import ru.ksergey.todolist.model.TodoItem;
import java.util.List;

public interface TodoItemDao {
    List<TodoItem> getAllTodoItems();
    TodoItem createTodoItem(TodoItem todoItem);
}

```

```java
package ru.ksergey.todolist.dao;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.ksergey.todolist.model.TodoItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

import java.sql.ResultSet;
import java.util.Objects;

@Repository
public class TodoItemDaoImpl implements TodoItemDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<TodoItem> todoItemRowMapper = (ResultSet rs, int rowNum) -> {
        TodoItem todoItem = new TodoItem();
        todoItem.setId(rs.getLong("id"));
        todoItem.setTitle(rs.getString("title"));
        todoItem.setDescription(rs.getString("description"));
        todoItem.setStatus(TodoItem.TodoStatus.valueOf(rs.getString("status")));
        todoItem.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return todoItem;
    };

    @Override
    public List<TodoItem> getAllTodoItems() {
        String sql = "SELECT * FROM todo_items";
        return jdbcTemplate.query(sql, todoItemRowMapper);
    }

    @Override
    public TodoItem createTodoItem(TodoItem todoItem) {
        String sql = "INSERT INTO todo_items (title, description, status) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, todoItem.getTitle());
            ps.setString(2, todoItem.getDescription());
            ps.setString(3, todoItem.getStatus().name());
            return ps;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        todoItem.setId(id);

        String selectSql = "SELECT * FROM todo_items WHERE id = ?";
        return jdbcTemplate.queryForObject(selectSql, todoItemRowMapper, id);
    }
}
```

## 5. Создание контроллера

Создайте класс `TodoItemController` в пакете `ru.ksergey.todolist.controller`:

```java
package ru.ksergey.todolist.controller;  
  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.web.bind.annotation.*;  
import ru.ksergey.todolist.dao.TodoItemDao;  
import ru.ksergey.todolist.model.TodoItem;  
  
import java.util.List;  
  
@RestController  
@RequestMapping("/api/todo")  
public class TodoItemController {  
  
    @Autowired  
    private TodoItemDao todoItemDao;  
  
    @GetMapping("items")  
    public List<TodoItem> getAllTodoItems() {  
        return todoItemDao.getAllTodoItems();  
    }  
  
    @PostMapping  
    public TodoItem createTodoItem(@RequestBody TodoItem todoItem) {  
        return todoItemDao.createTodoItem(todoItem);  
    }  
}
```

## 6. Запуск приложения

1. Запустите приложение, выполнив метод `main` в классе `TodolistApplication`.
2. Приложение будет доступно по адресу `http://localhost:8080`.
3. GET-запрос на получение всех записей http://localhost:8080/api/todo/items
4. POST-запрос на добавление записи http://localhost:8080/api/todo/

Модель

```json
{
	"title": "Тестовые запись",
	"description": "Тестовое описание",
	"status": "IN_PROGRESS",
	"createdAt": "2024-06-28T03:48:12"
}
```

## 7.  Postman

Для выполнения POST-запроса можно воспользоваться [postman](https://www.postman.com)
1. Скачайте и установите
2. Импортируйте коллекцию `java-todo.postman_collection.json`
3. Выполните POST-запрос `add item`


# Техническое задание

Есть всё получилось можете потренироваться и доработать проект

### Расширение функциональности TodoItemController

Цель: Добавить в существующий TodoItemController методы для получения одной записи по ID и обновления существующей записи.

Задачи:

1. Реализация метода получения TodoItem по ID:
   - Создать новый метод в контроллере с названием `getTodoItemById`.
   - Использовать аннотацию @GetMapping с путём "/{id}".
   - Метод должен принимать параметр Long id с аннотацией @PathVariable.
   - Реализовать логику получения TodoItem из базы данных по указанному ID.
   - Вернуть найденный TodoItem. Если запись не найдена, выбросить исключение.

2. Реализация метода обновления TodoItem:
   - Создать новый метод в контроллере с названием `updateTodoItem`.
   - Использовать аннотацию @PutMapping с путём "/{id}".
   - Метод должен принимать два параметра:
     * Long id с аннотацией @PathVariable
     * TodoItem todoItem с аннотацией @RequestBody
   - Реализовать логику обновления существующей записи в базе данных.
   - Вернуть обновленный TodoItem. Если запись не найдена, выбросить исключение.

3. Обновление интерфейса TodoItemDao:
   - Добавить методы `getTodoItemById` и `updateTodoItem` в интерфейс.

4. Реализация новых методов в TodoItemDaoImpl:
   - Реализовать логику получения записи по ID в методе `getTodoItemById`.
   - Реализовать логику обновления записи в методе `updateTodoItem`.

5. Обработка ошибок:
   - Создать пользовательское исключение, например, `TodoItemNotFoundException`.
   - Реализовать глобальный обработчик исключений для корректной обработки ошибок.

Требования к реализации:
- Использовать Spring аннотации для маппинга HTTP-запросов.
- Обеспечить корректную обработку случаев, когда запись не найдена.
- Использовать DTO (Data Transfer Object) при необходимости для разделения логики представления и бизнес-логики.
- Следовать принципам RESTful API при проектировании эндпоинтов.

Ожидаемый результат:
- Работающие эндпоинты для получения TodoItem по ID и обновления существующего TodoItem.
