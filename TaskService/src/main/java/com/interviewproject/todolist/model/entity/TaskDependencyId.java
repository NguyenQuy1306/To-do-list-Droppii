package com.interviewproject.todolist.model.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class TaskDependencyId implements Serializable {
    private Long taskId;
    private Long dependencyId;
}
