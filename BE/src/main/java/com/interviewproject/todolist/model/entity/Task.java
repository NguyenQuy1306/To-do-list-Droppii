package com.interviewproject.todolist.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@DynamicInsert
@Table(name = "Task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taskId")
    private Long taskId;
}
