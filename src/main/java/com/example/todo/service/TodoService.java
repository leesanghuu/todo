package com.example.todo.service;

import com.example.todo.dto.AddTodoRequestDto;
import com.example.todo.dto.TodoResponseDto;
import com.example.todo.dto.UpdateRequestDto;
import com.example.todo.entity.Todo;
import com.example.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    public List<TodoResponseDto> getAllTodos() {
        return todoRepository.findAll().stream()
                .map(TodoResponseDto::fromEntity)
                .toList();
    }

    public TodoResponseDto getTodoById(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID 없음: " + id));
        return TodoResponseDto.fromEntity(todo);
    }

    public void updateTodo(Long id, UpdateRequestDto requestDto) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID 없음: " + id));

        // title이 null이 아닐 경우 변경
        if (requestDto.getTitle() != null) {
            todo.setTitle(requestDto.getTitle());
        }
        todoRepository.save(todo);
    }

    public void saveTodo(Todo todo) {
        todoRepository.save(todo);
    }

    public void toggleTodo(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("할 일을 찾을 수 없습니다. ID: " + id));
        todo.setCompleted(!todo.isCompleted());
        todoRepository.save(todo);
    }

    public void deleteTodo(Long id) {
        todoRepository.deleteById(id);
    }

    public List<TodoResponseDto> getTodosByDate(LocalDate date) {
        return todoRepository.findByDate(date).stream()
                .map(TodoResponseDto::fromEntity)
                .toList();
    }

    public List<TodoResponseDto> getTodosBetweenDates(LocalDate startDate, LocalDate endDate) {
        return todoRepository.findByDateBetween(startDate, endDate).stream()
                .map(TodoResponseDto::fromEntity)
                .toList();
    }

    public Long addTodo(AddTodoRequestDto requestDto) {
        if (requestDto.isOverwrite()) {
            List<Todo> existingTodos = todoRepository.findByDate(requestDto.getDate());
            todoRepository.deleteAll(existingTodos);
        }

        Todo newTodo = new Todo();
        newTodo.setDate(requestDto.getDate());
        newTodo.setTitle(requestDto.getTitle());
        newTodo.setCompleted(false);

        Todo saveTodo = todoRepository.save(newTodo);
        return saveTodo.getId();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduledRollOverUncompletedTodos() {
        rollOverUncompletedTodos(LocalDate.now().minusDays(1));
    }

    public void rollOverUncompletedTodos(LocalDate fromDate) {

        List<Todo> uncompletedTodos = todoRepository.findByDateAndCompletedFalse(fromDate);

        for (Todo todo : uncompletedTodos) {
            todo.setDate(fromDate.plusDays(1));
        }
        todoRepository.saveAll(uncompletedTodos);
    }
}
