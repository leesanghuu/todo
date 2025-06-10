package com.example.todo.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            if (jwtTokenProvider.validateToken(token)) {
                String userIdentifier = jwtTokenProvider.getUserIdentifier(token);
                System.out.println(">>> Setting user identifier: " + userIdentifier);
                UserContextHolder.setUserIdentifier(userIdentifier);
            } else {
                System.out.println(">>> Invalid token: " + token);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false; // 유효하지 않은 토큰 거절
            }
        } else {
            // Authorization 헤더 없으면 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 요청 처리 후 ThreadLocal 클리어
        UserContextHolder.clear();
    }
}

