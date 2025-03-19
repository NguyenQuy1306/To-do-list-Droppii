package com.interviewproject.todolist.service.impl;

import com.interviewproject.todolist.service.TaskService;
import com.interviewproject.todolist.specification.TaskSpecification;
import com.interviewproject.todolist.exception.TodoException;
import com.interviewproject.todolist.model.entity.Task;
import com.interviewproject.todolist.model.entity.TaskDependency;
import com.interviewproject.todolist.model.entity.TaskDependencyId;
import com.interviewproject.todolist.model.entity.TaskStatus;
import com.interviewproject.todolist.model.mapper.TaskMapper;
import com.interviewproject.todolist.model.request.TaskRequest;
import com.interviewproject.todolist.model.request.TaskUpdateRequest;
import com.interviewproject.todolist.model.response.TaskResponse;
import com.interviewproject.todolist.repository.TaskDependencyRepository;
import com.interviewproject.todolist.repository.TaskRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    @Autowired
    private TaskDependencyRepository taskDependencyRepository;

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

    private void updateStatusTask(Long taskId, TaskStatus taskStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TodoException("Task not found", "TASK_NOT_FOUND", HttpStatus.NOT_FOUND));
        for (TaskDependency taskDependency : task.getDependencies()) {
            if (taskDependency.getDependentTask().getStatus() != TaskStatus.COMPLETED) {
                throw new TodoException("Cannot start task until all dependencies are completed",
                        "DEPENDENCY_NOT_COMPLETED", HttpStatus.BAD_REQUEST);
            }
        }
        task.setStatus(taskStatus);
        taskRepository.save(task);
    }

    private Task getTaskById(Long taskId, String message, String errorCode) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TodoException(message, errorCode, HttpStatus.NOT_FOUND));
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
                throw new TodoException("Invalid status", "INVALID_STATUS", HttpStatus.BAD_REQUEST);
            }
            updateStatusTask(task.getTaskId(), request.getStatus());
        }

        task = taskRepository.save(task);
        return taskMapper.toTaskResponse(task);
    }

    @Override
    public void deleteTask(Long taskId) {
        Task task = getTaskById(taskId, "Task not found", "TASK_NOT_FOUND");
        taskRepository.delete(task);
    }

    @Override
    public void addDependency(Long taskId, Long dependencyId) {
        Task task = getTaskById(taskId, "Task not found", "TASK_NOT_FOUND");
        Task dependencyTask = getTaskById(dependencyId, "Dependency Task not found", "DEPENDENCY_TASK_NOT_FOUND");
        TaskDependencyId taskDependencyId = new TaskDependencyId(taskId, dependencyId);

        if (taskDependencyRepository.existsById(taskDependencyId)) {
            throw new TodoException("Dependency already exists", "DEPENDENCY_EXISTS", HttpStatus.BAD_REQUEST);
        }
        TaskDependency taskDependency = TaskDependency.builder().id(taskDependencyId).dependentTask(dependencyTask)
                .task(task).build();
        taskDependencyRepository.save(taskDependency);
    }

    @Override
    public void deleteDependency(Long taskId, Long dependencyId) {
        getTaskById(taskId, "Task not found", "TASK_NOT_FOUND");
        getTaskById(dependencyId, "Dependency Task not found", "DEPENDENCY_TASK_NOT_FOUND");
        TaskDependencyId taskDependencyId = new TaskDependencyId(taskId, dependencyId);
        if (!taskDependencyRepository.existsById(taskDependencyId)) {
            throw new TodoException("Dependency does not exist", "DEPENDENCY_NOT_EXISTS", HttpStatus.BAD_REQUEST);
        }
        taskDependencyRepository.deleteById(taskDependencyId);
    }

    private void getALlDependencies(Set<List<Long>> dependencies, List<Long> currentPath, Long taskId) {
        List<TaskDependency> directDependencies = taskDependencyRepository.findByIdTaskId(taskId);
        if (directDependencies.isEmpty()) {
            dependencies.add(new ArrayList<>(currentPath));
            return;
        }
        for (TaskDependency taskDependency : directDependencies) {
            Long dependencyId = taskDependency.getId().getDependencyId();
            // Check trong List sau đó add vào nếu chưa có.
            if (!currentPath.contains(dependencyId)) {
                currentPath.add(dependencyId);
                getALlDependencies(dependencies, currentPath, dependencyId);
                currentPath.remove(currentPath.size() - 1);
            }
        }
    }

    @Override
    public Set<List<Long>> getDependencies(Long taskId) {
        getTaskById(taskId, "Task not found", "TASK_NOT_FOUND");
        Set<List<Long>> allDependencies = new HashSet<>();
        getALlDependencies(allDependencies, new ArrayList<>(List.of(taskId)), taskId);
        return allDependencies;
    }

}
