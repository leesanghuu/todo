package com.example.todo.controller;

import com.example.todo.dto.RefreshRequestDto;
import com.example.todo.dto.TokenResponseDto;
import com.example.todo.exception.AccessDeniedException;
import com.example.todo.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/token")
    public ResponseEntity<TokenResponseDto> issueToken() {
        // userIdentifier로 UUID 생성
        String userIdentifier = UUID.randomUUID().toString();

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createToken(userIdentifier);
        String refreshToken = jwtTokenProvider.createRefreshToken(userIdentifier);


        return ResponseEntity.ok(new TokenResponseDto(accessToken, refreshToken));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponseDto> reissueToken(@RequestBody RefreshRequestDto requestDto) {
        String refreshToken = requestDto.getRefreshToken();

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new AccessDeniedException("Refresh token이 유효하지 않습니다.");
        }

        String userIdentifier = jwtTokenProvider.getUserIdentifier(refreshToken);
        String newAccessToken = jwtTokenProvider.createToken(userIdentifier);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userIdentifier);

        return ResponseEntity.ok(new TokenResponseDto(newAccessToken, newRefreshToken));
    }
}
