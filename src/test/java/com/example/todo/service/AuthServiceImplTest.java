package com.example.todo.service;

import com.example.todo.dto.TokenResponseDto;
import com.example.todo.entity.RefreshToken;
import com.example.todo.exception.AccessDeniedException;
import com.example.todo.jwt.JwtTokenProvider;
import com.example.todo.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void issueToken_정상발급() {
        // given
        String userIdentifier = UUID.randomUUID().toString();
        String accessToken = "mockAccessToken";
        String refreshToken = "mockRefreshToken";

        // mock JwtTokenProvider 동작 설정
        when(jwtTokenProvider.createToken(anyString())).thenReturn(accessToken);
        when(jwtTokenProvider.createRefreshToken(anyString())).thenReturn(refreshToken);

        // when
        TokenResponseDto response = authService.issueToken();

        // then
        assertThat(response.getAccessToken()).isEqualTo(accessToken);
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken);

        // verify → save가 호출되었는지 확인
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void reissueToken_정상재발급() {
        // given
        String refreshToken = "validRefreshToken";
        String userIdentifier = UUID.randomUUID().toString();

        RefreshToken savedToken = new RefreshToken(userIdentifier, refreshToken);

        String newAccessToken = "newAccessToken";
        String newRefreshToken = "newRefreshToken";

        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUserIdentifier(refreshToken)).thenReturn(userIdentifier);
        when(refreshTokenRepository.findById(userIdentifier)).thenReturn(Optional.of(savedToken));
        when(jwtTokenProvider.createToken(userIdentifier)).thenReturn(newAccessToken);
        when(jwtTokenProvider.createRefreshToken(userIdentifier)).thenReturn(newRefreshToken);

        // when
        TokenResponseDto response = authService.reissueToken(refreshToken);

        // then
        assertThat(response.getAccessToken()).isEqualTo(newAccessToken);
        assertThat(response.getRefreshToken()).isEqualTo(newRefreshToken);
    }

    @Test
    void reissueToken_유효하지않으면_예외발생() {
        // given
        String refreshToken = "invalidRefreshToken";

        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.reissueToken(refreshToken))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("유효하지 않습니다.");
    }
}