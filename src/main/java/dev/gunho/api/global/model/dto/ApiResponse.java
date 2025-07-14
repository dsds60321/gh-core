package dev.gunho.api.global.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final boolean success;
    private final String message;
    private final String code;
    private final T data;
    private final Object error;
    private final long timestamp;

    private ApiResponse(boolean success, String message, String code, T data, Object error) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.data = data;
        this.error = error;
        this.timestamp = System.currentTimeMillis();
    }

    // 성공 응답
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다.", "SUCCESS", data, null);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, "SUCCESS", data, null);
    }

    // 실패 응답
    public static <T> ApiResponse<T> failure(String message, String code) {
        return new ApiResponse<>(false, message, code, null, null);
    }

    public static <T> ApiResponse<T> failure(String message, String code, Object error) {
        return new ApiResponse<>(false, message, code, null, error);
    }

    // 검증 오류 응답
    public static <T> ApiResponse<T> validationError(Object validationErrors) {
        return new ApiResponse<>(false, "입력값 검증에 실패했습니다.", "VALIDATION_ERROR", null, validationErrors);
    }

    // 시스템 오류 응답
    public static <T> ApiResponse<T> systemError(String message) {
        return new ApiResponse<>(false, message, "SYSTEM_ERROR", null, null);
    }
}
