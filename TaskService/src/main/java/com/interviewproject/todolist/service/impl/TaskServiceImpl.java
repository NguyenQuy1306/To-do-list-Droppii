package com.interviewproject.todolist.service.impl;

import com.interviewproject.todolist.service.KafkaProducerService;
import com.interviewproject.todolist.service.RedisService;
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

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
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
    @Autowired
    private ApplicationContext context;
    @Autowired
    private KafkaProducerService kafkaProducerService;
    @Autowired
    private RedisService redisService;

    private TaskResponse toTaskResponse(Task task) {
        return TaskResponse.builder().description(task.getDescription()).dueDate(task.getDueDate()).priority(task.getPriority()).status(task.getStatus()).taskId(task.getTaskId()).title(task.getTitle()).build();
    }

    @CacheEvict(value = "tasks", allEntries = true)
    @Override
    public TaskResponse createTask(TaskRequest request) {
        try {
            if (taskRepository.existsByTitle(request.getTitle())) {
                throw new TodoException("Task already exists", "TASK_EXISTS", HttpStatus.BAD_REQUEST);
            }

            Task task = Task.builder().title(request.getTitle()).description(request.getDescription()).dueDate(request.getDueDate()).priority(request.getPriority()).status(TaskStatus.PENDING).build();

            task = taskRepository.save(task);

            return toTaskResponse(task);
        } catch (TodoException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("Error creating task: " + e);
            log.error("Error creating task: ", e);
            throw new TodoException("Internal Server Error", "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Cacheable(value = "tasks", key = "'tasks:' + (#title != null ? #title : 'all') + ':' + (#status != null ? #status.name() : 'null') + ':' + (#priority != null ? #priority : 'null') + ':' + (#dueDate != null ? #dueDate.toString() : 'null')")
    public List<TaskResponse> getCachedTaskList(String title, TaskStatus status, Integer priority, LocalDate dueDate) {
        Specification<Task> spec = TaskSpecification.filterTasks(title, status, priority, dueDate);
        List<Task> tasks = taskRepository.findAll(spec);
        if (tasks.isEmpty()) {
            throw new TodoException("No tasks found", "TASK_NOT_FOUND", HttpStatus.NOT_FOUND);
        }
        return tasks.stream().map(task -> TaskResponse.builder().taskId(task.getTaskId()).dueDate(task.getDueDate()).description(task.getDescription()).priority(task.getPriority()).status(task.getStatus()).title(task.getTitle()).build()).collect(Collectors.toList());
    }

    @Override
    public Page<TaskResponse> listTasks(String title, TaskStatus status, Integer priority, LocalDate dueDate, Pageable pageable) {
        TaskServiceImpl proxy = context.getBean(TaskServiceImpl.class);
        List<TaskResponse> cachedList = proxy.getCachedTaskList(title, status, priority, dueDate);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), cachedList.size());
        List<TaskResponse> pagedList = cachedList.subList(start, end);
        return new PageImpl<>(pagedList, pageable, cachedList.size());
    }

    private boolean isValidStatusTransition(TaskStatus currentStatus, TaskStatus newStatus) {
        // Tạo danh sách trạng thái sau cho phép update.
        Map<TaskStatus, List<TaskStatus>> validTransitions = Map.of(TaskStatus.PENDING, List.of(TaskStatus.IN_PROGRESS, TaskStatus.CANCELLED), TaskStatus.IN_PROGRESS, List.of(TaskStatus.COMPLETED, TaskStatus.CANCELLED), TaskStatus.COMPLETED, List.of());
        return validTransitions.getOrDefault(currentStatus, List.of()).contains(newStatus);
    }

    private void updateStatusTask(Long taskId, TaskStatus taskStatus) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TodoException("Task not found", "TASK_NOT_FOUND", HttpStatus.NOT_FOUND));
        for (TaskDependency taskDependency : task.getDependencies()) {
            if (taskDependency.getDependentTask().getStatus() != TaskStatus.COMPLETED) {
                throw new TodoException("Cannot start task until all dependencies are completed", "DEPENDENCY_NOT_COMPLETED", HttpStatus.BAD_REQUEST);
            }
        }
        task.setStatus(taskStatus);
        taskRepository.save(task);
    }

    private Task getTaskById(Long taskId, String message, String errorCode) {
        return taskRepository.findById(taskId).orElseThrow(() -> new TodoException(message, errorCode, HttpStatus.NOT_FOUND));
    }

    @CacheEvict(value = "tasks", allEntries = true)
    @Override
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TodoException("Task not found", "TASK_NOT_FOUND", HttpStatus.NOT_FOUND));

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
        return toTaskResponse(task);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    @Override
    public void deleteTask(Long taskId) {
        Task task = getTaskById(taskId, "Task not found", "TASK_NOT_FOUND");
        taskRepository.delete(task);
    }

    private Boolean checkCycle(Long taskId, Long dependencyId) {
        List<TaskDependency> directDependencies = taskDependencyRepository.findByIdTaskId(dependencyId);
        if (directDependencies.isEmpty()) {
            return false;
        }

        for (TaskDependency taskDependency : directDependencies) {
            Long currentId = taskDependency.getDependentTask().getTaskId();
            if (currentId.equals(taskId)) {
                return true;
            }
            if (checkCycle(taskId, currentId)) {
                return true;
            }

        }
        return false;
    }

    @Transactional
    public void addDependency(Long taskId, Long dependencyId) {
        Task task = getTaskById(taskId, "Task not found", "TASK_NOT_FOUND");
        Task dependencyTask = getTaskById(dependencyId, "Dependency Task not found", "DEPENDENCY_TASK_NOT_FOUND");
        TaskDependencyId taskDependencyId = new TaskDependencyId(taskId, dependencyId);

        if (taskDependencyRepository.existsById(taskDependencyId)) {
            throw new TodoException("Dependency already exists", "DEPENDENCY_EXISTS", HttpStatus.BAD_REQUEST);
        }
        Boolean checkCycle = checkCycle(taskId, dependencyId);
        if (checkCycle) {
            throw new TodoException("Adding this dependency would create a cycle", "CIRCULAR_DEPENDENCY", HttpStatus.BAD_REQUEST);
        }
        TaskDependency taskDependency = TaskDependency.builder().id(taskDependencyId).dependentTask(dependencyTask).task(task).build();
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

    @Cacheable(value = "tasks", key = "#taskId")
    @Override
    public TaskResponse findById(Long taskId) {
        Task task = getTaskById(taskId, "Task not found", "TASK_NOT_FOUND");
        return taskMapper.toResponse(task);
    }

    public void sendTaskStatus(Long taskId, String topic, String message) {
        if (!redisService.isTaskSent(taskId, topic)) {
            redisService.markTaskAsSent(taskId, topic);
            kafkaProducerService.sendMessage(topic, message);
        } else {
            return;
        }
    }

    @Scheduled(fixedRate = 60000)
    @Override
    public void checkOverdueTasks() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime upcomingThreshold = now.plusHours(6);
        List<Task> tasks = taskRepository.findByDueDateBefore(upcomingThreshold);

        System.out.println("🔄 Scheduled task running at: " + now);
        tasks.parallelStream().forEach(task -> {
            String topic = "";
            String message = "";
            if (task.getDueDate().isBefore(now)) {
                topic = "task_notifications_overdue";
                message = "Task quá hạn: " + task.getTitle() + " vào lúc: " + task.getDueDate();
            } else {
                topic = "task_notifications_upcoming";
                message = "Task sắp đến hạn: " + task.getTitle() + " vào lúc: " + task.getDueDate();
            }
            sendTaskStatus(task.getTaskId(), topic, message);
        });


    }
}
