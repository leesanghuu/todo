package com.example.todo.service;

import com.example.todo.dto.AddTodoRequestDto;
import com.example.todo.dto.TodoResponseDto;
import com.example.todo.dto.UpdateRequestDto;
import com.example.todo.entity.Todo;
import com.example.todo.exception.AccessDeniedException;
import com.example.todo.exception.ResourceNotFoundException;
import com.example.todo.jwt.UserContextHolder;
import com.example.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    public List<TodoResponseDto> getAllTodos() {
        String userIdentifier = UserContextHolder.getUserIdentifier();

        return todoRepository.findByUserIdentifier(userIdentifier).stream()
                .map(TodoResponseDto::fromEntity)
                .toList();
    }

    public TodoResponseDto getTodoById(Long id) {
        Todo todo = getAuthorizedTodo(id);
        return TodoResponseDto.fromEntity(todo);
    }

    public TodoResponseDto updateTodo(Long id, UpdateRequestDto requestDto) {
        Todo todo = getAuthorizedTodo(id);

        // title이 null이 아닐 경우 변경
        if (requestDto.getTitle() != null) {
            todo.setTitle(requestDto.getTitle());
        }
        Todo updatedTodo = todoRepository.save(todo);

        return TodoResponseDto.fromEntity(updatedTodo);
    }

    public void saveTodo(Todo todo) {
        todoRepository.save(todo);
    }

    public void toggleTodo(Long id) {
        Todo todo = getAuthorizedTodo(id);
        todo.setCompleted(!todo.isCompleted());
        todoRepository.save(todo);
    }

    public void deleteTodo(Long id) {
        Todo todo = getAuthorizedTodo(id);
        todoRepository.delete(todo);
    }

    public List<TodoResponseDto> getTodosByDate(LocalDate date) {
        String userIdentifier = UserContextHolder.getUserIdentifier();

        return todoRepository.findByUserIdentifierAndDate(userIdentifier, date).stream()
                .map(TodoResponseDto::fromEntity)
                .toList();
    }

    public List<TodoResponseDto> getTodosBetweenDates(LocalDate startDate, LocalDate endDate) {
        String userIdentifier = UserContextHolder.getUserIdentifier();
        return todoRepository.findByUserIdentifierAndDateBetween(userIdentifier, startDate, endDate).stream()
                .map(TodoResponseDto::fromEntity)
                .toList();
    }

    public Map<LocalDate, List<TodoResponseDto>> getTodosGroupedByDate() {
        String userIdentifier = UserContextHolder.getUserIdentifier();

        List<Todo> todos = todoRepository.findByUserIdentifier(userIdentifier);

        return todos.stream()
                .collect(Collectors.groupingBy(
                        Todo::getDate,
                        Collectors.mapping(TodoResponseDto::fromEntity, Collectors.toList())
                ));
    }

    public Long addTodo(AddTodoRequestDto requestDto) {
        String userIdentifier = UserContextHolder.getUserIdentifier(); // 현재 사용자 ID 추출

        if (requestDto.isOverwrite()) {
            List<Todo> existingTodos = todoRepository.findByUserIdentifierAndDate(userIdentifier, requestDto.getDate());
            todoRepository.deleteAll(existingTodos);
        }

        Todo newTodo = new Todo();
        newTodo.setDate(requestDto.getDate());
        newTodo.setTitle(requestDto.getTitle());
        newTodo.setCompleted(false);
        newTodo.setUserIdentifier(userIdentifier);

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

    private Todo getAuthorizedTodo(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID 없음: " + id));

        if (!todo.getUserIdentifier().equals(UserContextHolder.getUserIdentifier())) {
            throw new AccessDeniedException("현재 사용자에게 접근 권한이 없습니다.");
        }

        return todo;
    }
}
