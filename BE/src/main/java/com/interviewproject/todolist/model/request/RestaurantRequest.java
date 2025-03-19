package com.interviewproject.todolist.model.request;

import java.io.Serializable;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantRequest implements Serializable {

    private Long courseId;

    private String title;

    private String description;

    private Long price;

    private Long instructorId;

    private Long categoryId;

}
