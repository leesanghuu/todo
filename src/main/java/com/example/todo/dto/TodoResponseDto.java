package com.example.todo.dto;

import com.example.todo.entity.Todo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class TodoResponseDto {

    private Long id;
    private String title;
    private boolean completed;
    private LocalDate date;

    public static TodoResponseDto fromEntity(Todo todo) {
        return new TodoResponseDto(
                todo.getId(),
                todo.getTitle(),
                todo.isCompleted(),
                todo.getDate()
        );
    }
}
