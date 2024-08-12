package ru.ksergey.todolist.dto;

import lombok.Data;
import ru.ksergey.todolist.model.TodoItem;

@Data
public class UpdateToDoDto {
    private String title;
    private String description;
    private TodoItem.TodoStatus status;
}
