package ru.ksergey.todolist.dao;

import ru.ksergey.todolist.model.TodoItem;
import java.util.List;

public interface TodoItemDao {
    List<TodoItem> getAllTodoItems();

    TodoItem createTodoItem(TodoItem todoItem);

    TodoItem getTodoItemById(Long id);

    TodoItem updateTodoItem(Long id, TodoItem todoItem);
}
