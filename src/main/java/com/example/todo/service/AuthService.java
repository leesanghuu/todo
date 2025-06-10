package com.example.todo.service;

import com.example.todo.dto.TokenResponseDto;

public interface AuthService {
    TokenResponseDto issueToken();
    TokenResponseDto reissueToken(String refreshToken);
}
