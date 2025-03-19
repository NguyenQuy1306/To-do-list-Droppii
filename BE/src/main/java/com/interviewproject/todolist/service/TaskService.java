package com.interviewproject.todolist.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import com.interviewproject.todolist.model.entity.TaskStatus;
import com.interviewproject.todolist.model.request.TaskRequest;
import com.interviewproject.todolist.model.request.TaskUpdateRequest;
import com.interviewproject.todolist.model.response.TaskResponse;

public interface TaskService {
    public TaskResponse createTask(TaskRequest request);

    public Page<TaskResponse> listTasks(String title,
            TaskStatus status,
            Integer priority,
            LocalDate dueDate,
            Pageable pageable);

    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request);

    public void deleteTask(Long taskId);
}
