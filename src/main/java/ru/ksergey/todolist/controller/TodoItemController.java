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