package com.example.todo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class RefreshToken {

    @Id
    private String userIdentifier;

    private String refreshToken;

    public RefreshToken(String userIdentifier, String refreshToken) {
        this.userIdentifier = userIdentifier;
        this.refreshToken = refreshToken;
    }

    public void updateToken(String newToken) {
        this.refreshToken = newToken;
    }
}
