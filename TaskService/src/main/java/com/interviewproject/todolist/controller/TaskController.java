package com.interviewproject.todolist.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import com.interviewproject.todolist.exception.TodoException;
import com.interviewproject.todolist.model.entity.TaskStatus;
import com.interviewproject.todolist.model.request.TaskRequest;
import com.interviewproject.todolist.model.request.TaskUpdateRequest;
import com.interviewproject.todolist.model.response.ApiResponse;
import com.interviewproject.todolist.model.response.MetadataResponse;
import com.interviewproject.todolist.model.response.TaskResponse;
import com.interviewproject.todolist.service.TaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
            apiResponse.error(Map.of("message", "Unexpected error occurred: " + e));
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
            Page<TaskResponse> tasksPage = taskService.listTasks(title, status, priority, dueDate, pageable);

            MetadataResponse metadata = new MetadataResponse(
                    tasksPage.getTotalElements(),
                    tasksPage.getTotalPages(),
                    tasksPage.getNumber(),
                    tasksPage.getSize(),
                    (tasksPage.hasNext() ? "/api/tasks?page=" + (tasksPage.getNumber() + 1) : null),
                    (tasksPage.hasPrevious() ? "/api/tasks?page=" + (tasksPage.getNumber() - 1) : null),
                    "/api/tasks?page=" + (tasksPage.getTotalPages() - 1),
                    "/api/tasks?page=0");

            Map<String, Object> responseMetadata = new HashMap<>();
            responseMetadata.put("pagination", metadata);
            apiResponse.ok(tasksPage.getContent(), responseMetadata);
        } catch (TodoException e) {
            apiResponse.error(Map.of("message", e.getMessage()));
            return ResponseEntity.status(e.getStatus()).body(apiResponse);
        } catch (Exception e) {
            apiResponse.error(Map.of("message", "Unexpected error occurred: " + e));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> putMethodName(@PathVariable Long taskId,
            @RequestBody @Valid TaskUpdateRequest taskRequest) {
        ApiResponse<TaskResponse> apiResponse = new ApiResponse<>();

        try {
            TaskResponse taskResponse = taskService.updateTask(taskId, taskRequest);
            apiResponse.ok(taskResponse);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (TodoException e) {
            apiResponse.error(Map.of("message", e.getMessage()));
            return ResponseEntity.status(e.getStatus()).body(apiResponse);
        } catch (Exception e) {
            apiResponse.error(Map.of("message", "Unexpected error occurred"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Object>> deleteTask(@PathVariable Long taskId) {
        ApiResponse<Object> apiResponse = new ApiResponse<>();
        try {
            taskService.deleteTask(taskId);
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("message", "Task deleted successfully");
            apiResponse.ok(null, metadata);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (TodoException e) {
            apiResponse.error(Map.of("message", e.getMessage()));
            return ResponseEntity.status(e.getStatus()).body(apiResponse);
        } catch (Exception e) {
            apiResponse.error(Map.of("message", "Unexpected error occurred"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @PostMapping("{taskId}/dependencies/{dependencyId}")
    public ResponseEntity<ApiResponse<Object>> createDependency(@PathVariable Long taskId,
            @PathVariable Long dependencyId) {
        ApiResponse<Object> apiResponse = new ApiResponse<>();
        try {
            taskService.addDependency(taskId, dependencyId);
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("message", "Dependency is added successfully");
            apiResponse.ok(null, metadata);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (TodoException e) {
            apiResponse.error(Map.of("message", e.getMessage()));
            return ResponseEntity.status(e.getStatus()).body(apiResponse);
        } catch (Exception e) {
            apiResponse.error(Map.of("message", "Unexpected error occurred"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @DeleteMapping("{taskId}/dependencies/{dependencyId}")
    public ResponseEntity<ApiResponse<Object>> deleteDependency(@PathVariable Long taskId,
            @PathVariable Long dependencyId) {
        ApiResponse<Object> apiResponse = new ApiResponse<>();
        try {
            taskService.deleteDependency(taskId, dependencyId);
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("message", "Dependency is deleted successfully");
            apiResponse.ok(null, metadata);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (TodoException e) {
            apiResponse.error(Map.of("message", e.getMessage()));
            return ResponseEntity.status(e.getStatus()).body(apiResponse);
        } catch (Exception e) {
            apiResponse.error(Map.of("message", "Unexpected error occurred"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @GetMapping("/{taskId}/dependencies")
    public ResponseEntity<ApiResponse<Set<List<Long>>>> deleteDependency(@PathVariable Long taskId) {
        ApiResponse<Set<List<Long>>> apiResponse = new ApiResponse<>();
        try {
            Set<List<Long>> dependencies = taskService.getDependencies(taskId);

            apiResponse.ok(dependencies);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (TodoException e) {
            apiResponse.error(Map.of("message", e.getMessage()));
            return ResponseEntity.status(e.getStatus()).body(apiResponse);
        } catch (Exception e) {
            apiResponse.error(Map.of("message", "Unexpected error occurred"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }

    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getUserById(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.findById(taskId));
    }
}
