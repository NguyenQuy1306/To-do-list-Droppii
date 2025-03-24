package com.interviewproject.todolist.service.impl;

import com.interviewproject.todolist.service.RedisService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean isTaskSent(Long taskId, String topic) {
        return redisTemplate.hasKey(String.valueOf(taskId) + "_" + topic);
    }

    @Override
    public void markTaskAsSent(Long taskId, String topic) {
        redisTemplate.opsForValue().set(String.valueOf(taskId) + "_" + topic, "sent", 5, TimeUnit.MINUTES);
    }
}
