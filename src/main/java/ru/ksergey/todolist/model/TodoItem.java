package ru.ksergey.todolist.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoItem {
    private Long id;
    private String title;
    private String description;
    private TodoStatus status;
    private LocalDateTime createdAt;

    public enum TodoStatus {
        PENDING, IN_PROGRESS, COMPLETED
    }
}
