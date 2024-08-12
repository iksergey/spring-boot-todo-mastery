package ru.ksergey.todolist.exceptions;

public class ToDoNoExistException extends RuntimeException {
    public ToDoNoExistException(String message) {
        super(message);
    }
}
