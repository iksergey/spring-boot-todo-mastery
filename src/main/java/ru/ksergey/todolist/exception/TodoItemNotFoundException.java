package ru.ksergey.todolist.exception;

public class TodoItemNotFoundException extends RuntimeException {
    public TodoItemNotFoundException(String message) {
        super(message);
    }
}
