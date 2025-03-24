package com.interviewproject.todolist.model.request;

import java.io.Serializable;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.interviewproject.todolist.model.entity.TaskStatus;

@Getter
@Setter
public class TaskUpdateRequest implements Serializable {

    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @FutureOrPresent(message = "Due date must be in the future or today")
    private LocalDateTime dueDate;

    @Min(value = 1, message = "Priority must be at least 1")
    @Max(value = 5, message = "Priority must be at most 5")
    private Integer priority;

    private TaskStatus status;
}
