package com.example.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AddTodoRequestDto {

    @NotNull
    private LocalDate date;

    @NotBlank
    private String title;

    @NotNull
    private boolean overwrite;
}
