package com.example.todo.controller;

import com.example.todo.dto.AddTodoRequestDto;
import com.example.todo.dto.UpdateRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TodoRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addTodo_할일추가() throws Exception {
        AddTodoRequestDto requestDto = buildAddTodoRequest("테스트 할 일");

        mockMvc.perform(post("/api/todos")
                        .header("Authorization", "Bearer " + 발급받은토큰())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void getAllTodos_전체조회() throws Exception {
        mockMvc.perform(get("/api/todos")
                        .header("Authorization", "Bearer " + 발급받은토큰()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getTodosByDate_날짜별조회() throws Exception {
        LocalDate today = LocalDate.now();

        mockMvc.perform(get("/api/todos")
                        .header("Authorization", "Bearer " + 발급받은토큰())
                        .param("date", today.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getTodosBetweenDates_날짜범위조회() throws Exception {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(3);

        mockMvc.perform(get("/api/todos/range")
                        .header("Authorization", "Bearer " + 발급받은토큰())
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateTodo_수정() throws Exception {
        String token = 발급받은토큰();
        // 먼저 할 일 추가 -> id 확보
        AddTodoRequestDto addRequest = buildAddTodoRequest("Original title");

        String response = mockMvc.perform(post("/api/todos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(response).get("id").asLong();

        // 수정 요청
        UpdateRequestDto updateRequest = new UpdateRequestDto();
        updateRequest.setTitle("New title");

        mockMvc.perform(put("/api/todos/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void toggleTodo_토글() throws Exception {
        String token = 발급받은토큰();
        // 먼저 할 일 추가
        AddTodoRequestDto addRequest = buildAddTodoRequest("Original title");

        String response = mockMvc.perform(post("/api/todos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(response).get("id").asLong();

        // 토글 요청
        mockMvc.perform(patch("/api/todos/" + id + "/toggle")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTodo_삭제() throws Exception {
        String token  = 발급받은토큰();
        // 먼저 할 일 추가
        AddTodoRequestDto addRequest = buildAddTodoRequest("Original title");

        String response = mockMvc.perform(post("/api/todos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(response).get("id").asLong();

        // 삭제 요청
        mockMvc.perform(delete("/api/todos/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void addTodo_overwrite_기능확인() throws Exception {
        LocalDate date = LocalDate.now();
        String token = 발급받은토큰();

        // 첫번째 할 일 추가
        AddTodoRequestDto firstRequest = buildAddTodoRequest("First Todo");
        firstRequest.setDate(date);

        mockMvc.perform(post("/api/todos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isOk());

        // 두번째 할 일 추가 with overwrite
        AddTodoRequestDto overwriteRequest = buildAddTodoRequest("Overwritten Todo");
        overwriteRequest.setDate(date);
        overwriteRequest.setOverwrite(true);

        mockMvc.perform(post("/api/todos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overwriteRequest)))
                .andExpect(status().isOk());

        // 조회 시 "Overwritten"만 있어야 함
        mockMvc.perform(get("/api/todos")
                        .header("Authorization", "Bearer " + token)
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Overwritten Todo"));
    }


    @Test
    void updateTodo_없는아이디_404() throws Exception {
        UpdateRequestDto updateRequest = new UpdateRequestDto();
        updateRequest.setTitle("New title");

        mockMvc.perform(put("/api/todos/99999")
                        .header("Authorization", "Bearer " + 발급받은토큰())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTodo_없는아이디_404() throws Exception {
        mockMvc.perform(delete("/api/todos/99999")
                        .header("Authorization", "Bearer " + 발급받은토큰()))
                .andExpect(status().isNotFound());
    }

    @Test
    void toggleTodo_없는아이디_404() throws Exception {
        mockMvc.perform(patch("/api/todos/99999/toggle")
                        .header("Authorization", "Bearer " + 발급받은토큰()))
                .andExpect(status().isNotFound());
    }

    @Test
    void addTodo_필수값누락_400() throws Exception {
        // date 누락
        String invalidRequest = """
            {
                "title": "Invalid Todo",
                "overwrite": false
            }
            """;

        mockMvc.perform(post("/api/todos")
                        .header("Authorization", "Bearer " + 발급받은토큰())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 다른사용자_투두_수정_403() throws Exception {
        // 사용자 A 토큰 발급
        String tokenA = 발급받은토큰();
        // 사용자 B 토큰 발급
        String tokenB = 발급받은토큰();

        // 사용자 A로 투두 추가
        long id = 추가하고Id반환(tokenA, "User A Todo");

        // 사용자 B가 A의 Todo 수정 시도 → 403 기대
        UpdateRequestDto updateRequest = new UpdateRequestDto();
        updateRequest.setTitle("Hacked title");

        mockMvc.perform(put("/api/todos/" + id)
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void 다른사용자_투두_삭제_403() throws Exception {
        String tokenA = 발급받은토큰();
        String tokenB = 발급받은토큰();
        long id = 추가하고Id반환(tokenA, "User A Todo");

        mockMvc.perform(delete("/api/todos/" + id)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void 다른사용자_투두_토글_403() throws Exception {
        String tokenA = 발급받은토큰();
        String tokenB = 발급받은토큰();

        long id = 추가하고Id반환(tokenA, "User A Todo");

        mockMvc.perform(patch("/api/todos/" + id + "/toggle")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }



    // === 유틸 메서드 추가 ===
    private String 발급받은토큰() throws Exception {
        String tokenResponse = mockMvc.perform(post("/api/users/token"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(tokenResponse).get("accessToken").asText();
    }

    private long 추가하고Id반환(String token, String title) throws Exception {
        AddTodoRequestDto addRequest = new AddTodoRequestDto();
        addRequest.setDate(LocalDate.now());
        addRequest.setTitle(title);
        addRequest.setOverwrite(false);

        String response = mockMvc.perform(post("/api/todos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    private AddTodoRequestDto buildAddTodoRequest(String title) {
        AddTodoRequestDto dto = new AddTodoRequestDto();
        dto.setDate(LocalDate.now());
        dto.setTitle(title);
        dto.setOverwrite(false);
        return dto;
    }


}