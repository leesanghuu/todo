package com.example.todo.controller;

import com.example.todo.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserTokenController {

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> generateToken() {
        // userIdentifier로 UUID 생성
        String userIdentifier = UUID.randomUUID().toString();

        // JWT 토큰 생성
        String token = jwtTokenProvider.createToken(userIdentifier);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("userIdentifier", userIdentifier);

        return ResponseEntity.ok(response);
    }
}
