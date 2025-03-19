package com.interviewproject.todolist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interviewproject.todolist.model.entity.TaskDependency;
import com.interviewproject.todolist.model.entity.TaskDependencyId;

@Repository
public interface TaskDependencyRepository extends JpaRepository<TaskDependency, TaskDependencyId> {
}
