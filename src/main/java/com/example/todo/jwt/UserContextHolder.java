package com.example.todo.jwt;

import com.example.todo.exception.AccessDeniedException;

public class UserContextHolder {

    private static final ThreadLocal<String> userIdentifierHolder = new ThreadLocal<>();

    public static void setUserIdentifier(String userIdentifier) {
        userIdentifierHolder.set(userIdentifier);
    }

    public static String getUserIdentifier() {
        String userIdentifier = userIdentifierHolder.get();
        if (userIdentifier == null) {
            throw new AccessDeniedException("인증되지 않은 사용자입니다.");
        }
        return userIdentifierHolder.get();
    }

    public static void clear() {
        userIdentifierHolder.remove();
    }
}
