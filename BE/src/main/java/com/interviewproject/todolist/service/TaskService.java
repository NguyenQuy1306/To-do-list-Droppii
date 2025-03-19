package com.interviewproject.todolist.service;

import com.interviewproject.todolist.model.request.TaskRequest;
import com.interviewproject.todolist.model.response.TaskResponse;

public interface TaskService {
    public TaskResponse createTask(TaskRequest request);

}
