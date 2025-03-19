package com.interviewproject.todolist.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interviewproject.todolist.exception.TodoException;
import com.interviewproject.todolist.model.entity.TaskStatus;
import com.interviewproject.todolist.model.request.TaskRequest;
import com.interviewproject.todolist.model.response.ApiResponse;
import com.interviewproject.todolist.model.response.MetadataResponse;
import com.interviewproject.todolist.model.response.TaskResponse;
import com.interviewproject.todolist.service.TaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getListTasks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Integer priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        ApiResponse<List<TaskResponse>> apiResponse = new ApiResponse<>();
        try {
            Pageable pageable = PageRequest.of(page, size);

            Page<TaskResponse> tasks = taskService.listTasks(title, status, priority, dueDate, pageable);
            MetadataResponse metadata = new MetadataResponse(
                    tasks.getTotalElements(),
                    tasks.getTotalPages(),
                    tasks.getNumber(),
                    tasks.getSize(),
                    (tasks.hasNext()
                            ? "/api/tasks?page=" + (tasks.getNumber() +
                                    1)
                            : null),
                    (tasks.hasPrevious() ? "/api/tasks?page=" +
                            (tasks.getNumber() - 1) : null),
                    "/api/tasks?page=" + (tasks.getTotalPages() - 1),
                    "/api/tasks?page=0");
            Map<String, Object> responseMetadata = new HashMap<>();

            responseMetadata.put("pagination", metadata);
            apiResponse.ok(tasks.getContent(), responseMetadata);
        } catch (TodoException e) {
            apiResponse.error(Map.of("message", e.getMessage()));
            return ResponseEntity.status(e.getStatus()).body(apiResponse);
        } catch (Exception e) {
            apiResponse.error(Map.of("message", "Unexpected error occurred"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
