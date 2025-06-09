package com.example.todo.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // 서버에서 관리하는 secret key
    private final long tokenValidityInMilliSeconds = 1000L * 60 * 60 * 24 * 30; // 만료기간 30일

    // JWT 생성
    public String createToken(String userIdentifier) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInMilliSeconds);

        return Jwts.builder()
                .setSubject("user") // 고정 subject
                .claim("userIdentifier", userIdentifier) // Payload에 userIdentifier 포함
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
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
