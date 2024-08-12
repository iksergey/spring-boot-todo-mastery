package ru.ksergey.todolist.dao;

import ru.ksergey.todolist.dto.UpdateToDoDto;
import ru.ksergey.todolist.model.TodoItem;
import java.util.List;

public interface TodoItemDao {
    List<TodoItem> getAllTodoItems();

    TodoItem createTodoItem(TodoItem todoItem);

    TodoItem updateTodoItem(UpdateToDoDto todoItem, Long id);

    TodoItem getTodoItemById(Long id);
}
