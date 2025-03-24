package com.interviewproject.todolist.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

public interface KafkaProducerService {

    public void sendMessage(String topic, String message);
}