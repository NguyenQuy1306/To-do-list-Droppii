package com.interviewproject.todolist.model.request;

import java.io.Serializable;
import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;
import lombok.Data;
import java.time.LocalDateTime;

@Getter
@Setter
public class TaskRequest implements Serializable {

    @NotBlank(message = "Title cannot be empty")
    private String title;
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    @FutureOrPresent(message = "Due date must be in the future or today")
    private LocalDateTime dueDate;

    @NotNull(message = "Priority is required")
    @Min(value = 1, message = "Priority must be at least 1")
    @Max(value = 5, message = "Priority must be at most 5")
    private int priority;

}
