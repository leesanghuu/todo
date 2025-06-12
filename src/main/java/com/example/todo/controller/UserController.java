package com.example.todo.controller;

import com.example.todo.dto.RefreshRequestDto;
import com.example.todo.dto.TokenResponseDto;
import com.example.todo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @PostMapping("/token")
    public ResponseEntity<TokenResponseDto> issueToken() {
        TokenResponseDto tokenResponse = authService.issueToken();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Strict")
                .maxAge(14 * 24 * 60 * 60)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new TokenResponseDto(tokenResponse.getAccessToken(), null));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponseDto> reissueToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {

        TokenResponseDto tokenResponse = authService.reissueToken(refreshToken);
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Strict")
                .maxAge(14 * 24 * 60 * 60)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new TokenResponseDto(tokenResponse.getAccessToken(), null));
    }
}
