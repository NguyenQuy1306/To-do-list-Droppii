package com.interviewproject.todolist.service.impl;

import com.interviewproject.todolist.service.TaskService;
import com.interviewproject.todolist.exception.TodoException;
import com.interviewproject.todolist.model.entity.Task;
import com.interviewproject.todolist.model.entity.TaskStatus;
import com.interviewproject.todolist.model.mapper.TaskMapper;
import com.interviewproject.todolist.model.request.TaskRequest;
import com.interviewproject.todolist.model.response.TaskResponse;
import com.interviewproject.todolist.repository.TaskRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskMapper taskMapper;
    private final TaskRepository taskRepository;

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
}
