package com.example.todo.controller;

import com.example.todo.entity.Todo;
import com.example.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // REST API 컨트롤러
@RequestMapping("/api/todos")
@RequiredArgsConstructor // 생성자 자동 생성 -> DI
public class TodoRestController {
    private final TodoService todoService;

    // 전체 할 일 목록 조회 (GET /api/todos)
    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() {
        return ResponseEntity.ok(todoService.getAllTodos());
    }

    // 새로운 할 일 추가 (POST /api/todos)
    @PostMapping
    public ResponseEntity<Todo> addTodo(@RequestBody Todo todo) {
        todoService.saveTodo(todo);
        return ResponseEntity.ok(todo);
    }

    // 할 일 수정 (PUT /api/todos/{id})
    @PutMapping("/{id}") // PUT: 전체 필드 수정
    public ResponseEntity<String> updateTodo(@PathVariable Long id, @RequestBody Todo todo) {
        todoService.updateTodo(id, todo.getTitle());
        return ResponseEntity.ok("success");
    }

    // 할 일 완료 상태 변경 (PATCH /api/todos/{id}/toggle)
    @PatchMapping("/{id}/toggle") // PATCH: 필드 부분 수정
    public ResponseEntity<Todo> toggleTodo(@PathVariable Long id) {
        todoService.toggleTodo(id);
        return ResponseEntity.ok(todoService.getTodoById(id));
    }

    // 할 일 삭제 (DELETE /api/todos/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo((id));
        return ResponseEntity.noContent().build(); // 클라이언트의 요청 성공, 반환할 데이터는 없음
    }
}
