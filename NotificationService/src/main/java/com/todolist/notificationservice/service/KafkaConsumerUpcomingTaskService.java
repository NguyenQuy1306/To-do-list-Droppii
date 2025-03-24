package com.todolist.notificationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface KafkaConsumerUpcomingTaskService {
    public void listen(String message) throws JsonProcessingException;
}
