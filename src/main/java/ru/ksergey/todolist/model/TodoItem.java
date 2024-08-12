package ru.ksergey.todolist.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
