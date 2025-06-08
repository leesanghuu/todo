package com.example.todo.controller;

import com.example.todo.dto.AddTodoRequestDto;
import com.example.todo.dto.CreateTodoResponseDto;
import com.example.todo.dto.TodoResponseDto;
import com.example.todo.dto.UpdateRequestDto;
import com.example.todo.entity.Todo;
import com.example.todo.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Todo API", description = "할 일 관리 API")
@RestController // REST API 컨트롤러
@RequestMapping("/api/todos")
@RequiredArgsConstructor// 생성자 자동 생성 -> DI
public class TodoRestController {
    private final TodoService todoService;

    // 전체 할 일 목록 조회 (GET /api/todos)
    @GetMapping
    public ResponseEntity<List<TodoResponseDto>> getAllTodos(@RequestParam(required = false) LocalDate date) {
        // date 들어오면 해당 날짜 조회, 아니면 전체 조회
        if (date != null) {
            return ResponseEntity.ok(todoService.getTodosByDate(date));
        } else {
            return ResponseEntity.ok(todoService.getAllTodos());
        }
    }

    // 날짜 범위 조회 (GET /api/todos/range?startDate=...&endDate=...)
    @GetMapping("/range")
    public ResponseEntity<List<TodoResponseDto>> getTodosBetweenDates(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        return ResponseEntity.ok(todoService.getTodosBetweenDates(startDate, endDate));
    }

    // 새로운 할 일 추가 (POST /api/todos)
    @Operation(
            summary = "할 일 추가",
            description = "새로운 할 일을 등록합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<CreateTodoResponseDto> addTodo(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "등록할 할 일 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AddTodoRequestDto.class))
            )
            @RequestBody AddTodoRequestDto addTodoRequestDto
    ) {

        Long todoId = todoService.addTodo(addTodoRequestDto);
        return ResponseEntity.ok(new CreateTodoResponseDto(todoId));
    }

    // 할 일 수정 (PUT /api/todos/{id})
    @PutMapping("/{id}") // PUT: 전체 필드 수정
    public ResponseEntity<String> updateTodo(@PathVariable Long id, @RequestBody UpdateRequestDto requestDto) {
        todoService.updateTodo(id, requestDto);
        return ResponseEntity.ok("success");
    }

    // 할 일 완료 상태 변경 (PATCH /api/todos/{id}/toggle)
    @PatchMapping("/{id}/toggle") // PATCH: 필드 부분 수정
    public ResponseEntity<TodoResponseDto> toggleTodo(@PathVariable Long id) {
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
