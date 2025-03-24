package com.interviewproject.todolist.service.impl;

import com.interviewproject.todolist.service.KafkaProducerService;
import com.interviewproject.todolist.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "task_notifications";

    @Autowired
    public KafkaProducerServiceImpl(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
        System.out.println("📤 Đã gửi tin nhắn đến Kafka: " + message);
    }
}
