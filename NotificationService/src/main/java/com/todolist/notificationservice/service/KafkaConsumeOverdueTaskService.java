package com.todolist.notificationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface KafkaConsumeOverdueTaskService {
    public void listen(String message) throws JsonProcessingException;
}
