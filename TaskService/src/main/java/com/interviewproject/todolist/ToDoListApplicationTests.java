package com.interviewproject.todolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@ComponentScan(basePackages = "com.interviewproject.todolist")
@EnableJpaRepositories(basePackages = "com.interviewproject.todolist.repository")
public class ToDoListApplicationTests {
    public static void main(String[] args) {
        SpringApplication.run(ToDoListApplicationTests.class, args);
    }
}