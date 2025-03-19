package com.interviewproject.todolist.model.response;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.interviewproject.todolist.model.entity.TaskStatus;

import lombok.*;

@Getter
@Setter
@Builder
public class TaskResponse implements Serializable {
    private Long taskId;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Integer priority;
    private TaskStatus status;
}
