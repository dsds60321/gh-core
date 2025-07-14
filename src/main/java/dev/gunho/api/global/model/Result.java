package dev.gunho.api.global.model;

import lombok.Getter;

@Getter
public abstract class Result<T> {

    public static final class Success<T> extends Result<T> {
        private final T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }

    @Getter
    public static final class Failure<T> extends Result<T> {
        private final String message;
        private final String code;

        public Failure(String message, String code) {
            this.message = message;
            this.code = code;
        }

    }

    // 팩토리 메서드들
    public static <T> Result<T> success(T data) {
        return new Success<>(data);
    }

    public static <T> Result<T> failure(String message, String code) {
        return new Failure<>(message, code);
    }

    public static <T> Result<T> failure(String message) {
        return new Failure<>(message, "BUSINESS_ERROR");
    }

    // 유틸리티 메서드들
    public boolean isSuccess() {
        return this instanceof Success;
    }

    public boolean isFailure() {
        return this instanceof Failure;
    }

    public T getData() {
        if (this instanceof Success<T> success) {
            return success.getData();
        }
        throw new IllegalStateException("Cannot get data from failure result");
    }

    public String getMessage() {
        if (this instanceof Failure<T> failure) {
            return failure.getMessage();
        }
        return null;
    }

    public String getCode() {
        if (this instanceof Failure<T> failure) {
            return failure.getCode();
        }
        return null;
    }
}
