package com.example.todo.service;

import com.example.todo.entity.Todo;
import com.example.todo.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // 테스트 후 데이터 롤백(테스트 종료 후 DB 변경 사항 자동 제거)
class TodoServiceTest {

    @Autowired
    private TodoService todoService;
    @Autowired
    private TodoRepository todoRepository;

    /*
    할 일 전체 조회 테스트
     */
    @Test
    void testGetAllTodos() {
        //given (할 일 추가)
        Todo todo1 = new Todo();
        todo1.setTitle("할 일1");
        todo1.setCompleted(false);

        Todo todo2 = new Todo();
        todo2.setTitle("할 일2");
        todo2.setCompleted(false);

        todoService.saveTodo(todo1);
        todoService.saveTodo(todo2);

        //when (목록 조회)
        List<Todo> todos = todoService.getAllTodos();

        //then (데이터 검증)
        assertEquals(2, todos.size());
        assertEquals("할 일1", todos.get(0).getTitle());
        assertEquals("할 일2", todos.get(1).getTitle());
    }
    /*
    할 일 추가 테스트
     */
    @Test
    void testSaveTodo() {
        //given (새로운 할 일)
        Todo newTodo = new Todo();
        newTodo.setTitle("테스트 할 일 추가");
        newTodo.setCompleted(false);

        //when (할 일 저장)
        todoService.saveTodo(newTodo);

        //then (저장된 데이터 검증)
        List<Todo> todos = todoRepository.findAll();
        assertEquals(1, todos.size());
        assertEquals("테스트 할 일 추가", todos.get(0).getTitle());
        assertFalse(todos.get(0).isCompleted());
    }
    /*
    할 일 부분 조회(ID로 조회)
     */
    @Test
    void testGetTodoById() {
        //given (할 일 추가)
        Todo todo = new Todo();
        todo.setTitle("할 일 상세 조회");
        todo.setCompleted(false);
        todoService.saveTodo(todo);
        Long todoId = todo.getId();

        //when (ID로 조회)
        Todo foundTodo = todoService.getTodoById(todoId);

        //then (찾은 데이터 검증)
        assertNotNull(foundTodo);
        assertEquals("할 일 상세 조회", foundTodo.getTitle());
    }
    /*
    할 일 부분 조회 - 예외 테스트
     */
    @Test
    void testGetTodoById_NotFound() {
        //given (존재하지 않는 ID)
        Long todoId = 999L;

        //when, then (존재하지 않는 ID 조회 시도할 때 예외 발생 검증)
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            todoService.getTodoById(todoId);
        });
        assertEquals("해당 ID 없음: " + todoId, e.getMessage());
    }
    /*
    할 일 수정 테스트
     */
    @Test
    void testUpdateTodo() {
        //given (할 일 추가)
        Todo todo = new Todo();
        todo.setTitle("수정 전 할 일");
        todo.setCompleted(false);
        todoRepository.save(todo);

        Long todoId = todo.getId();

        //when (제목 수정)
        todoService.updateTodo(todoId, "수정된 할 일");

        //then (검증)
        Todo updateTodo = todoService.getTodoById(todoId);
        assertEquals("수정된 할 일", updateTodo.getTitle());
    }
    /*
    할 일 수정 - 예외 처리 테스트
     */
    @Test
    void testUpdateTodo_NotFound() {
        //given (존재하지 않는 ID)
        Long todoId = 999L;

        //when, then (존재하지 않는 ID의 투두 수정 - 예외 발생 검증)
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            todoService.updateTodo(todoId, "없는 할 일 수정");
        });
        assertEquals("해당 ID 없음: " + todoId, e.getMessage());
    }
    /*
    할 일 완료 여부 테스트
     */
    @Test
    void testToggleTodo() {
        //given (할 일 추가)
        Todo todo = new Todo();
        todo.setTitle("완료 상태 변경");
        todo.setCompleted(false);
        todoRepository.save(todo);
        Long todoId = todo.getId();

        //when (완료 상태 변경)
        todoService.toggleTodo(todoId);

        //then (검증)
        todoService.toggleTodo(todoId);
        Todo toggleBackTodo = todoService.getTodoById(todoId);
        assertFalse(toggleBackTodo.isCompleted());
    }
    /*
    할 일 완료 여부 예외 테스트
     */
    @Test
    void testToggleTodo_NotFound() {
        //given (존재하지 않는 ID)
        Long todoId = 999L;

        //when, then (예외 발생 검증)
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            todoService.toggleTodo(todoId);
        });
        assertEquals("할 일을 찾을 수 없습니다. ID: " + todoId, e.getMessage());
    }
    /*
    할 일 삭제 테스트
     */
    @Test
    void testDeleteTodo() {
        //given (할 일 추가)
        Todo todo = new Todo();
        todo.setTitle("삭제 테스트");
        todo.setCompleted(false);
        todoRepository.save(todo);

        Long todoId = todo.getId();

        //when (할 일 삭제)
        todoService.deleteTodo(todoId);

        //then (삭제 후 다시 조회 -> 데이터 없는거 확인)
        Optional<Todo> deleteTodo = todoRepository.findById(todoId);
        assertFalse(deleteTodo.isPresent());
    }



}