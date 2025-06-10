package com.example.todo.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // 서버에서 관리하는 secret key
    private final long tokenValidity = 1000L * 60 * 30; // 만료기간 30분

    // JWT 생성
    public String createToken(String userIdentifier) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidity);

        return Jwts.builder()
                .setSubject("user") // 고정 subject
                .claim("userIdentifier", userIdentifier) // Payload에 userIdentifier 포함
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
    }

    // Refresh Token 발급
    public String createRefreshToken(String userIdentifier) {
        Claims claims = Jwts.claims().setSubject(userIdentifier);

        Date now = new Date();
        Date validity = new Date(now.getTime() + 14L * 24 * 60 * 60 * 1000L); // 14일

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT에서 userIdentifier 추출
    public String getUserIdentifier(String token) {
        return parseClaims(token).getBody().get("userIdentifier", String.class);
    }

    // JWT 유효성 검증
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }


}
