package com.example.todo.service;

import com.example.todo.dto.AddTodoRequestDto;
import com.example.todo.dto.TodoResponseDto;
import com.example.todo.dto.UpdateRequestDto;
import com.example.todo.entity.Todo;
import com.example.todo.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {
    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    @Test
    void addTodo_저장() {
        // given
        AddTodoRequestDto requestDto = new AddTodoRequestDto();
        requestDto.setDate(LocalDate.of(2025, 6, 9));
        requestDto.setTitle("테스트 할 일");
        requestDto.setOverwrite(false);

        // when
        todoService.addTodo(requestDto);

        // then
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void updateTodo_수정() {
        // given
        Long todoId = 1L;
        Todo todo = new Todo();
        todo.setId(todoId);
        todo.setTitle("Old Title");

        UpdateRequestDto requestDto = new UpdateRequestDto();
        requestDto.setTitle("New Title");

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));

        // when
        todoService.updateTodo(todoId, requestDto);

        // then
        assertEquals("New Title", todo.getTitle());
    }

    @Test
    void toggleTodo_토글() {
        // given
        Long todoId = 1L;
        Todo todo = new Todo();
        todo.setId(todoId);
        todo.setCompleted(false);

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));

        // when
        todoService.toggleTodo(todoId);

        // then
        assertTrue(todo.isCompleted());
    }

    @Test
    void rollOverUncompletedTodos_미완료넘기기() {
        // given
        LocalDate fromDate = LocalDate.of(2025, 6, 9);

        Todo todo1 = new Todo();
        todo1.setDate(fromDate);
        todo1.setCompleted(false);

        List<Todo> uncompletedTodos = List.of(todo1);

        when(todoRepository.findByDateAndCompletedFalse(fromDate, todo1.isCompleted())).thenReturn(uncompletedTodos);

        // when
        todoService.rollOverUncompletedTodos(fromDate);

        // then
        assertEquals(fromDate.plusDays(1), todo1.getDate());
    }

    @Test
    void getTodosByDate_날짜별조회() {
        // given
        LocalDate date = LocalDate.of(2025, 6, 9);

        Todo todo = new Todo();
        todo.setId(1L);
        todo.setDate(date);
        todo.setTitle("할 일");
        todo.setCompleted(false);

        when(todoRepository.findByDate(date)).thenReturn(List.of(todo));

        // when
        List<TodoResponseDto> result = todoService.getTodosByDate(date);

        // then
        assertEquals(1, result.size());
        assertEquals(todo.getTitle(), result.get(0).getTitle());
    }
  
}