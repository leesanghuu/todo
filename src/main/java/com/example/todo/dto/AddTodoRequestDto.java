package com.example.todo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AddTodoRequestDto {

    private LocalDate date;
    private String title;
    private boolean completed;
    private boolean overwrite;
}
