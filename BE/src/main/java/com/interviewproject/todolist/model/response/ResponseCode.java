package com.interviewproject.todolist.model.response;

import java.util.HashMap;
import java.util.Map;

public class ResponseCode {
    public static Map<String, String> getError(Integer code) {
        Map<String, String> map = new HashMap<String, String>();
        switch (code) {
            case 1:
                map.put("MSG 1", "Invalid request: missing or incorrect format");
                break;
            case 2:
                map.put("MSG 2", "Task already exists");
                break;
            case 3:
                map.put("MSG 3", "Invalid task priority");
                break;
            case 4:
                map.put("MSG 4", "Invalid due date format");
                break;
            case 5:
                map.put("MSG 5", "Task not found");
                break;
            case 6:
                map.put("MSG 6", "Cannot delete task with active dependencies");
                break;
            case 7:
                map.put("MSG 7", "Dependency cycle detected");
                break;
            case 8:
                map.put("MSG 8", "Cannot start task: dependencies not completed");
                break;
            case 9:
                map.put("MSG 9", "Pagination parameters are invalid");
                break;
            case 10:
                map.put("MSG 10", "Task status update failed");
                break;
            case 11:
                map.put("MSG 11", "Task dependency not found");
                break;
            case 12:
                map.put("MSG 12", "Task already has this dependency");
                break;
            case 13:
                map.put("MSG 13", "Failed to remove dependency");
                break;
            case 14:
                map.put("MSG 14", "Error retrieving task dependencies");
                break;
            case 15:
                map.put("MSG 15", "Task update validation failed");
                break;
            case 16:
                map.put("MSG 16", "Caching error occurred");
                break;
            case 17:
                map.put("MSG 17", "Database query optimization required");
                break;
            case 18:
                map.put("MSG 18", "Failed to send notification");
                break;
            case 19:
                map.put("MSG 19", "Overdue task notification error");
                break;
            case 20:
                map.put("MSG 20", "Task creation failed due to server error");
                break;
            case 21:
                map.put("MSG 21", "Unexpected error occurred, please try again later");
                break;
            case 22:
                map.put("MSG 22", "Invalid request payload");
                break;
            case 23:
                map.put("MSG 23", "Internal Server Error");
                break;
            default:
                map.put("MSG ?", "Unknown error");
                break;
        }
        return map;
    }
}