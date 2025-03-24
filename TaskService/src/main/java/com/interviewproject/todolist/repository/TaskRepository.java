package com.interviewproject.todolist.repository;

import jakarta.validation.constraints.FutureOrPresent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.interviewproject.todolist.model.entity.Task;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    boolean existsByTitle(String title);

    @Query("SELECT DISTINCT R FROM Task R WHERE R.dueDate BETWEEN :now AND :upcomingThreshold")
    List<Task> findByDueDateBetween(@Param("now") LocalDateTime now, @Param("upcomingThreshold") LocalDateTime upcomingThreshold);

    List<Task> findByDueDateBefore(LocalDateTime dueDateBefore);
}
