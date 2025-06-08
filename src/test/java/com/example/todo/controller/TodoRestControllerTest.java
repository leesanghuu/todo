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
        AddTodoRequestDto requestDto = new AddTodoRequestDto();
        requestDto.setDate(LocalDate.now());
        requestDto.setTitle("테스트 할 일");
        requestDto.setOverwrite(false);

        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void getAllTodos_전체조회() throws Exception {
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getTodosByDate_날짜별조회() throws Exception {
        LocalDate today = LocalDate.now();

        mockMvc.perform(get("/api/todos")
                .param("date", today.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getTodosBetweenDates_날짜범위조회() throws Exception {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(3);

        mockMvc.perform(get("/api/todos/range")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateTodo_수정() throws Exception {
        // 먼저 할 일 추가 -> id 확보
        AddTodoRequestDto addRequest = new AddTodoRequestDto();
        addRequest.setDate(LocalDate.now());
        addRequest.setTitle("Original title");
        addRequest.setOverwrite(false);

        String response = mockMvc.perform(post("/api/todos")
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void toggleTodo_토글() throws Exception {
        // 먼저 할 일 추가
        AddTodoRequestDto addRequest = new AddTodoRequestDto();
        addRequest.setDate(LocalDate.now());
        addRequest.setTitle("Original title");
        addRequest.setOverwrite(false);

        String response = mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(response).get("id").asLong();

        // 토글 요청
        mockMvc.perform(patch("/api/todos/" + id + "/toggle"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTodo_삭제() throws Exception {
        // 먼저 할 일 추가
        AddTodoRequestDto addRequest = new AddTodoRequestDto();
        addRequest.setDate(LocalDate.now());
        addRequest.setTitle("Original title");
        addRequest.setOverwrite(false);

        String response = mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(response).get("id").asLong();

        // 삭제 요청
        mockMvc.perform(delete("/api/todos/" + id))
                .andExpect(status().isNoContent());
    }

}