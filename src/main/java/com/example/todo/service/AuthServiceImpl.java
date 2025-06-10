package com.example.todo.service;

import com.example.todo.dto.TokenResponseDto;
import com.example.todo.entity.RefreshToken;
import com.example.todo.exception.AccessDeniedException;
import com.example.todo.jwt.JwtTokenProvider;
import com.example.todo.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public TokenResponseDto issueToken() {
        String userIdentifier = UUID.randomUUID().toString();

        String accessToken = jwtTokenProvider.createToken(userIdentifier);
        String refreshToken = jwtTokenProvider.createRefreshToken(userIdentifier);

        refreshTokenRepository.save(new RefreshToken(userIdentifier, refreshToken));

        return new TokenResponseDto(accessToken, refreshToken);
    }

    @Override
    public TokenResponseDto reissueToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new AccessDeniedException("Refresh token이 유효하지 않습니다.");
        }

        String userIdentifier = jwtTokenProvider.getUserIdentifier(refreshToken);

        RefreshToken savedToken = refreshTokenRepository.findById(userIdentifier)
                .orElseThrow(() -> new AccessDeniedException("Refresh token 정보가 없습니다."));

        if (!savedToken.getRefreshToken().equals(refreshToken)) {
            throw new AccessDeniedException("Refresh token이 유효하지 않습니다.");
        }

        String newAccessToken = jwtTokenProvider.createToken(userIdentifier);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userIdentifier);

        savedToken.updateToken(newRefreshToken);

        return new TokenResponseDto(newAccessToken, newRefreshToken);
    }
}
