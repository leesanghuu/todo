package com.example.todo.dto;

import com.example.todo.entity.Todo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TodoResponseDto {

    private Long id;
    private String title;
    private boolean completed;
    private LocalDate date;

    public static TodoResponseDto fromEntity(Todo todo) {
        TodoResponseDto dto = new TodoResponseDto();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setCompleted(todo.isCompleted());
        dto.setDate(todo.getDate());
        return dto;
    }
}
