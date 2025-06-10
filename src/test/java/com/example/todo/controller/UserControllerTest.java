package com.example.todo.controller;

import com.example.todo.dto.AddTodoRequestDto;
import com.example.todo.dto.RefreshRequestDto;
import com.example.todo.repository.TodoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TodoRepository todoRepository;

    @BeforeEach
    void cleanUp() {
        todoRepository.deleteAll();
    }

    @Test
    void token_정상발급() throws Exception {
        mockMvc.perform(post("/api/users/token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void refreshToken_정상재발급() throws Exception {
        // 1. 두 토큰 발급
        String tokenResponse = mockMvc.perform(post("/api/users/token"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String refreshToken = objectMapper.readTree(tokenResponse).get("refreshToken").asText();

        // 2. refresh token으로 재발급 요청
        RefreshRequestDto refreshRequest = new RefreshRequestDto();
        refreshRequest.setRefreshToken(refreshToken);

        mockMvc.perform(post("/api/users/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void refreshToken_유효하지않으면_403() throws Exception {
        RefreshRequestDto refreshRequest = new RefreshRequestDto();
        refreshRequest.setRefreshToken("잘못된 토큰");

        mockMvc.perform(post("/api/users/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void 사용자별_투두리스트_분리_테스트() throws Exception {
        // 1. 사용자 A 토큰 발급
        String tokenA = mockMvc.perform(post("/api/users/token"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String userAToken = objectMapper.readTree(tokenA).get("accessToken").asText();

        // 2. 사용자 B 토큰 발급
        String tokenB = mockMvc.perform(post("/api/users/token"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String userBToken = objectMapper.readTree(tokenB).get("accessToken").asText();

        // 3. 사용자 A로 투두 추가
        AddTodoRequestDto addRequestA = new AddTodoRequestDto();
        addRequestA.setDate(LocalDate.now());
        addRequestA.setTitle("User A");
        addRequestA.setOverwrite(false);

        mockMvc.perform(post("/api/todos")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequestA)))
                .andExpect(status().isOk());

        // 4. 사용자 B로 투두 추가
        AddTodoRequestDto addRequestB = new AddTodoRequestDto();
        addRequestB.setDate(LocalDate.now());
        addRequestB.setTitle("User B");
        addRequestB.setOverwrite(false);

        mockMvc.perform(post("/api/todos")
                        .header("Authorization", "Bearer " + userBToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequestB)))
                .andExpect(status().isOk());

        // 5. 사용자 A로 조회
        mockMvc.perform(get("/api/todos")
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("User A"));

        // 6. 사용자 A로 조회
        mockMvc.perform(get("/api/todos")
                        .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("User B"));
    }
}