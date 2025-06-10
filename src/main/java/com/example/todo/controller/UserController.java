package com.example.todo.controller;

import com.example.todo.dto.RefreshRequestDto;
import com.example.todo.dto.TokenResponseDto;
import com.example.todo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @PostMapping("/token")
    public ResponseEntity<TokenResponseDto> issueToken() {
        return ResponseEntity.ok(authService.issueToken());
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponseDto> reissueToken(@RequestBody RefreshRequestDto requestDto) {
       return ResponseEntity.ok(authService.reissueToken(requestDto.getRefreshToken()));
    }
}
