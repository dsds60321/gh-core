package dev.gunho.api.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {
    // 성공
    SUCCESS(200, "SUCCESS", "요청이 성공적으로 처리되었습니다."),
    CREATED(201, "CREATED", "리소스가 성공적으로 생성되었습니다."),

    // 클라이언트 오류
    BAD_REQUEST(400, "BAD_REQUEST", "잘못된 요청입니다."),
    UNAUTHORIZED(401, "UNAUTHORIZED", "인증이 필요합니다."),
    FORBIDDEN(403, "FORBIDDEN", "접근이 거부되었습니다."),
    NOT_FOUND(404, "NOT_FOUND", "요청한 리소스를 찾을 수 없습니다."),
    VALIDATION_ERROR(400, "VALIDATION_ERROR", "입력값 검증에 실패했습니다."),

    // 비즈니스 오류 (인증 관련)
    DUPLICATE_EMAIL(409, "DUPLICATE_EMAIL", "이미 존재하는 이메일입니다."),
    DUPLICATE_ID(409, "DUPLICATE_ID", "이미 존재하는 아이디입니다."),
    INVALID_CODE(400, "INVALID_CODE", "잘못된 인증 코드입니다."),
    CODE_EXPIRED(400, "CODE_EXPIRED", "인증 코드가 만료되었습니다."),
    REDIS_ERROR(500, "REDIS_ERROR", "Redis 저장소 오류가 발생했습니다."),

    // 서버 오류
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."),
    EMAIL_SEND_FAILED(500, "EMAIL_SEND_FAILED", "이메일 전송에 실패했습니다."),
    DATABASE_ERROR(500, "DATABASE_ERROR", "데이터베이스 오류가 발생했습니다."),

    // 세션 관련 오류
    SESSION_EXPIRED(401, "SESSION_EXPIRED", "세션이 만료되었습니다."),
    INVALID_SESSION(401, "INVALID_SESSION", "유효하지 않은 세션입니다."),
    SESSION_CREATE_FAILED(500, "SESSION_CREATE_FAILED", "세션 생성에 실패했습니다.");

    private final int httpStatus;
    private final String code;
    private final String message;

    // HttpStatus 반환을 위한 메서드
    public HttpStatus getHttpStatus() {
        return HttpStatus.valueOf(this.httpStatus);
    }

    /**
     * Result 코드를 ResponseCode로 변환하는 정적 메서드
     */
    public static ResponseCode fromResultCode(String resultCode) {
        if (resultCode == null) {
            return INTERNAL_SERVER_ERROR;
        }

        return switch (resultCode) {
            case "DUPLICATE_ID" -> DUPLICATE_ID;
            case "DUPLICATE_EMAIL" -> DUPLICATE_EMAIL;
            case "SIGNUP_ERROR" -> INTERNAL_SERVER_ERROR;
            case "BUSINESS_ERROR" -> BAD_REQUEST;
            case "VALIDATION_ERROR" -> VALIDATION_ERROR;
            case "DATABASE_ERROR" -> DATABASE_ERROR;
            case "REDIS_ERROR" -> REDIS_ERROR;
            case "EMAIL_SEND_FAILED" -> EMAIL_SEND_FAILED;
            case "SESSION_ERROR" -> SESSION_CREATE_FAILED;
            default -> INTERNAL_SERVER_ERROR;
        };
    }
}
