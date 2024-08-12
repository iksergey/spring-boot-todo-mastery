package ru.ksergey.todolist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.ksergey.todolist.dao.TodoItemDao;
import ru.ksergey.todolist.dto.UpdateToDoDto;
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

    @GetMapping("{id}")
    public TodoItem getTodoItemById(@PathVariable Long id) {
        return todoItemDao.getTodoItemById(id);
    }

    @PostMapping
    public TodoItem createTodoItem(@RequestBody TodoItem todoItem) {
        return todoItemDao.createTodoItem(todoItem);
    }

    @PostMapping("{id}")
    public TodoItem updateToDoItem(@RequestBody UpdateToDoDto todoItem, @PathVariable Long id){
        return todoItemDao.updateTodoItem(todoItem, id);
    }
}