package com.interviewproject.todolist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interviewproject.todolist.model.entity.TaskDependency;
import com.interviewproject.todolist.model.entity.TaskDependencyId;

@Repository
public interface TaskDependencyRepository extends JpaRepository<TaskDependency, TaskDependencyId> {
    List<TaskDependency> findByIdTaskId(Long taskId);

}
