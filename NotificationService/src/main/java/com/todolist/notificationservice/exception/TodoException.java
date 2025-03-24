package com.todolist.notificationservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TodoException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus status;

    public TodoException(String message, String errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
}