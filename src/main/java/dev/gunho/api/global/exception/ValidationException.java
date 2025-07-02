package dev.gunho.api.global.exception;

import lombok.Getter;
import java.util.List;
import java.util.Map;

@Getter
public class ValidationException extends RuntimeException {
    private final List<ValidationError> validationErrors;
    private final Map<String, Object> additionalInfo;

    /**
     * 기본 생성자
     */
    public ValidationException() {
        super("입력값 검증에 실패했습니다.");
        this.validationErrors = List.of();
        this.additionalInfo = Map.of();
    }

    /**
     * 메시지를 포함한 생성자
     */
    public ValidationException(String message) {
        super(message);
        this.validationErrors = List.of();
        this.additionalInfo = Map.of();
    }

    /**
     * 검증 오류 목록을 포함한 생성자
     */
    public ValidationException(List<ValidationError> validationErrors) {
        super("입력값 검증에 실패했습니다.");
        this.validationErrors = validationErrors != null ? validationErrors : List.of();
        this.additionalInfo = Map.of();
    }

    /**
     * 메시지와 검증 오류 목록을 포함한 생성자
     */
    public ValidationException(String message, List<ValidationError> validationErrors) {
        super(message);
        this.validationErrors = validationErrors != null ? validationErrors : List.of();
        this.additionalInfo = Map.of();
    }

    /**
     * 모든 정보를 포함한 생성자
     */
    public ValidationException(String message, List<ValidationError> validationErrors,
                               Map<String, Object> additionalInfo) {
        super(message);
        this.validationErrors = validationErrors != null ? validationErrors : List.of();
        this.additionalInfo = additionalInfo != null ? additionalInfo : Map.of();
    }

    /**
     * 메시지와 원인 예외를 포함한 생성자
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.validationErrors = List.of();
        this.additionalInfo = Map.of();
    }

    /**
     * 간단한 에러 메시지 목록 반환
     */
    public List<String> getErrorMessages() {
        return validationErrors.stream()
                .map(ValidationError::message)
                .toList();
    }

    /**
     * 필드별 에러 맵 반환
     */
    public Map<String, String> getFieldErrors() {
        return validationErrors.stream()
                .collect(java.util.stream.Collectors.toMap(
                        ValidationError::field,
                        ValidationError::message,
                        (existing, replacement) -> existing // 중복 키 처리
                ));
    }

    /**
     * 검증 오류 정보를 담는 레코드
     */
    public record ValidationError(
            String field,
            String message,
            Object rejectedValue,
            String code
    ) {
        public ValidationError(String field, String message) {
            this(field, message, null, null);
        }

        public ValidationError(String field, String message, Object rejectedValue) {
            this(field, message, rejectedValue, null);
        }
    }
}
