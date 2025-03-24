package com.todolist.notificationservice.handler;


import com.todolist.notificationservice.exception.TodoException;
import com.todolist.notificationservice.model.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        ApiResponse<Object> response = new ApiResponse<>();
        response.error(errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TodoException.class)
    public ResponseEntity<ApiResponse<Object>> handleTodoException(TodoException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());

        ApiResponse<Object> response = new ApiResponse<>();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("errorCode", ex.getErrorCode());
        metadata.put("timestamp", LocalDateTime.now());
        metadata.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());

        response.error(errors, metadata);

        return new ResponseEntity<>(response, ex.getStatus());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Database constraint violation");

        ApiResponse<Object> response = new ApiResponse<>();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("errorCode", "DATA_INTEGRITY_ERROR");
        metadata.put("timestamp", LocalDateTime.now());
        metadata.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());

        response.error(errors, metadata);

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleAllUncaughtException(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred", ex);

        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());

        ApiResponse<Object> response = new ApiResponse<>();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("errorCode", "INTERNAL_SERVER_ERROR");
        metadata.put("timestamp", LocalDateTime.now());
        metadata.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());

        response.error(errors, metadata);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
