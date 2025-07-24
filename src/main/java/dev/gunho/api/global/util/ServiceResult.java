package dev.gunho.api.global.util;

import dev.gunho.api.global.enums.ResponseCode;
import lombok.Getter;

@Getter
public class ServiceResult<T> {
    private final boolean success;
    private final T data;
    private final String message;
    private final ResponseCode responseCode;
    private final Throwable throwable;

    private ServiceResult(boolean success, T data, String message, ResponseCode responseCode, Throwable throwable) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.responseCode = responseCode;
        this.throwable = throwable;
    }

    // 성공 결과
    public static <T> ServiceResult<T> success(T data) {
        return new ServiceResult<>(true, data, ResponseCode.SUCCESS.getMessage(), ResponseCode.SUCCESS, null);
    }

    public static <T> ServiceResult<T> success(T data, String message) {
        return new ServiceResult<>(true, data, message, ResponseCode.SUCCESS, null);
    }

    // 실패 결과 - 와일드카드 타입 지원
    public static ServiceResult<?> failure(ResponseCode responseCode) {
        return new ServiceResult<>(false, null, responseCode.getMessage(), responseCode, null);
    }

    public static ServiceResult<?> failure(ResponseCode responseCode, String message) {
        return new ServiceResult<>(false, null, message, responseCode, null);
    }

    public static ServiceResult<?> failure(ResponseCode responseCode, Throwable throwable) {
        return new ServiceResult<>(false, null, responseCode.getMessage(), responseCode, throwable);
    }

    public static ServiceResult<?> failure(ResponseCode responseCode, String message, Throwable throwable) {
        return new ServiceResult<>(false, null, message, responseCode, throwable);
    }

    // 하위 호환성을 위한 Exception 메서드들
    public static ServiceResult<?> failure(ResponseCode responseCode, Exception exception) {
        return new ServiceResult<>(false, null, responseCode.getMessage(), responseCode, exception);
    }

    public static ServiceResult<?> failure(ResponseCode responseCode, String message, Exception exception) {
        return new ServiceResult<>(false, null, message, responseCode, exception);
    }

    // 기존 제네릭 타입 failure 메서드들 (하위 호환성)
    public static <T> ServiceResult<T> typedFailure(ResponseCode responseCode) {
        return new ServiceResult<>(false, null, responseCode.getMessage(), responseCode, null);
    }

    public static <T> ServiceResult<T> typedFailure(ResponseCode responseCode, String message) {
        return new ServiceResult<>(false, null, message, responseCode, null);
    }

    public static <T> ServiceResult<T> typedFailure(ResponseCode responseCode, Throwable throwable) {
        return new ServiceResult<>(false, null, responseCode.getMessage(), responseCode, throwable);
    }

    public static <T> ServiceResult<T> typedFailure(ResponseCode responseCode, String message, Throwable throwable) {
        return new ServiceResult<>(false, null, message, responseCode, throwable);
    }

    // 편의 메서드 - Exception 반환 (하위 호환성)
    public Exception getException() {
        return throwable instanceof Exception ? (Exception) throwable : new RuntimeException(throwable);
    }

    // 새로운 메서드 - Throwable 반환
    public Throwable getThrowable() {
        return throwable;
    }
}
