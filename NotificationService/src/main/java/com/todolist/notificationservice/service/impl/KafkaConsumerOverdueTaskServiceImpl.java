package com.todolist.notificationservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todolist.notificationservice.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerOverdueTaskServiceImpl implements com.todolist.notificationservice.service.KafkaConsumerUpcomingTaskService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    @KafkaListener(topics = "${task.notification.topic.overdueTask}", groupId = "notification-group")
    public void listen(String message) throws JsonProcessingException {
//        JsonNode rootNode = objectMapper.readTree(message);
//        String operation = rootNode.path("op").asText();
//        JsonNode afterNode = rootNode.path("after");
//        Notification notification = objectMapper.treeToValue(afterNode, Notification.class);
//        notificationRepository.save(notification);
        sendEmailNotification(message);
    }

    private void sendEmailNotification(String message) {
        System.out.println("Sending email reminder: " + message);

    }
}
