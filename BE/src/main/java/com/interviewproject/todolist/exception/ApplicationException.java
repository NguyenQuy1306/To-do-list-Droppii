package com.interviewproject.todolist.exception;

public class ApplicationException extends RuntimeException {
    public ApplicationException() {
    }

    public ApplicationException(String message) {
        super(message);
    }

}
