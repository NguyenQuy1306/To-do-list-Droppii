package com.interviewproject.todolist.service.impl;

import com.interviewproject.todolist.service.TaskService;
import com.interviewproject.todolist.specification.TaskSpecification;
import com.interviewproject.todolist.exception.TodoException;
import com.interviewproject.todolist.model.entity.Task;
import com.interviewproject.todolist.model.entity.TaskStatus;
import com.interviewproject.todolist.model.mapper.TaskMapper;
import com.interviewproject.todolist.model.request.TaskRequest;
import com.interviewproject.todolist.model.request.TaskUpdateRequest;
import com.interviewproject.todolist.model.response.TaskResponse;
import com.interviewproject.todolist.repository.TaskRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TaskRepository taskRepository;

    @Override
    public TaskResponse createTask(TaskRequest request) {
        try {
            if (taskRepository.existsByTitle(request.getTitle())) {
                throw new TodoException("Task already exists", "TASK_EXISTS", HttpStatus.BAD_REQUEST);
            }

            Task task = Task.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .dueDate(request.getDueDate())
                    .priority(request.getPriority())
                    .status(TaskStatus.PENDING)
                    .build();

            task = taskRepository.save(task);

            return taskMapper.toTaskResponse(task);
        } catch (TodoException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating task: ", e);
            throw new TodoException("Internal Server Error", "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Page<TaskResponse> listTasks(String title,
            TaskStatus status,
            Integer priority,
            LocalDate dueDate,
            Pageable pageable) {
        Specification<Task> spec = TaskSpecification.filterTasks(title, status, priority, dueDate);

        Page<Task> taskPage = taskRepository.findAll(spec, pageable);
        if (taskPage.isEmpty()) {
            throw new TodoException("No tasks found", "TASK_NOT_FOUND", HttpStatus.NOT_FOUND);
        }
        return taskPage.map(taskMapper::toTaskResponse);
    }

    private boolean isValidStatusTransition(TaskStatus currentStatus, TaskStatus newStatus) {
        // Tạo danh sách trạng thái sau cho phép update.
        Map<TaskStatus, List<TaskStatus>> validTransitions = Map.of(
                TaskStatus.PENDING, List.of(TaskStatus.IN_PROGRESS, TaskStatus.CANCELLED),
                TaskStatus.IN_PROGRESS, List.of(TaskStatus.COMPLETED, TaskStatus.CANCELLED),
                TaskStatus.COMPLETED, List.of());
        return validTransitions.getOrDefault(currentStatus, List.of()).contains(newStatus);
    }

    @Override
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TodoException("Task not found", "TASK_NOT_FOUND", HttpStatus.NOT_FOUND));

        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getStatus() != null) {
            if (!isValidStatusTransition(task.getStatus(), request.getStatus())) {
                throw new TodoException("Invalid status transition", "INVALID_STATUS", HttpStatus.BAD_REQUEST);
            }
            task.setStatus(request.getStatus());
        }

        task = taskRepository.save(task);
        return taskMapper.toTaskResponse(task);
    }

    @Override
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TodoException("Task not found", "TASK_NOT_FOUND", HttpStatus.NOT_FOUND));
        taskRepository.delete(task);
    }

}
