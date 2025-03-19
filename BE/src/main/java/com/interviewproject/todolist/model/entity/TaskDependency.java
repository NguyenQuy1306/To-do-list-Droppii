package com.interviewproject.todolist.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TaskDependency")
public class TaskDependency {

    @EmbeddedId
    private TaskDependencyId id;

    @ManyToOne
    @MapsId("taskId")
    @JoinColumn(name = "taskId", nullable = false)
    private Task task;

    @ManyToOne
    @MapsId("taskId")
    @JoinColumn(name = "dependencyId", nullable = false, referencedColumnName = "taskId")
    private Task dependentTask;
}
