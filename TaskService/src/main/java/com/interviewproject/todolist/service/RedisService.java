package com.interviewproject.todolist.service;

public interface RedisService {
    public boolean isTaskSent(Long taskId, String topic);

    public void markTaskAsSent(Long taskId, String topic);
}
