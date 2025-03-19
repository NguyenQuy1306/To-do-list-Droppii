package com.interviewproject.todolist.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interviewproject.todolist.exception.TodoException;
import com.interviewproject.todolist.model.request.TaskRequest;
import com.interviewproject.todolist.model.response.ApiResponse;
import com.interviewproject.todolist.model.response.TaskResponse;
import com.interviewproject.todolist.service.TaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/tasks")
@RequiredArgsConstructor
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(@Valid @RequestBody TaskRequest taskRequest) {
        ApiResponse<TaskResponse> apiResponse = new ApiResponse<>();

        try {
            TaskResponse taskResponse = taskService.createTask(taskRequest);
            apiResponse.ok(taskResponse);
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        } catch (TodoException e) {
            apiResponse.error(Map.of("message", e.getMessage()));
            return ResponseEntity.status(e.getStatus()).body(apiResponse);
        } catch (Exception e) {
            apiResponse.error(Map.of("message", "Unexpected error occurred"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

}
