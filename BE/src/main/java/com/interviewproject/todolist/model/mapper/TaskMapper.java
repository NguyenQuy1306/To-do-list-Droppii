package com.interviewproject.todolist.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

import com.interviewproject.todolist.model.entity.Task;
import com.interviewproject.todolist.model.response.TaskResponse;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(source = "task.taskId", target = "taskId")
    @Mapping(source = "task.title", target = "title")
    @Mapping(source = "task.description", target = "description")
    @Mapping(source = "task.dueDate", target = "dueDate")
    @Mapping(source = "task.priority", target = "priority")
    // @Mapping(source = "task.status", target = "status")
    TaskResponse toTaskResponse(Task task);

}
