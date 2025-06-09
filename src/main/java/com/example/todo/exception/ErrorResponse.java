package com.example.todo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private boolean success;
    private String code;
    private String message;

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(false, code, message);
    }
}
