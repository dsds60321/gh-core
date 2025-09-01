package dev.gunho.api.global.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class CustomException extends RuntimeException{
    private final Map<String, String> errors;

    public CustomException(String message) {
        super(message);
        this.errors = null;
    }

    public CustomException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }
}
