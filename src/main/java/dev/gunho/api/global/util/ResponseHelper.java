package dev.gunho.api.global.util;

import dev.gunho.api.global.exception.CustomException;
import dev.gunho.api.global.exception.ValidationException;
import dev.gunho.api.global.model.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
public class ResponseHelper {

    /**
     * 기본 성공 응답
     */
    public static Mono<ServerResponse> ok(ApiResponse<?> response) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response);
    }

    /**
     * 기본 실패 응답
     */
    public static Mono<ServerResponse> badRequest(ApiResponse<?> response) {
        return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response);
    }



    /**
     * 검증 오류 응답
     */
    public static Mono<ServerResponse> validationError(Object validationErrors) {
        return ServerResponse
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ApiResponse.validationError(validationErrors));
    }

    /**
     * 검증 오류 응답
     */
    public static Mono<ServerResponse> validationError(String message, Object validationErrors) {
        return ServerResponse
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ApiResponse.validationError(message, validationErrors));
    }

    /**
     * 시스템 오류 응답
     */
    public static Mono<ServerResponse> systemError(String message) {
        return ServerResponse
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ApiResponse.systemError(message));
    }

    /**
     * 예외별 응답 처리
     */
    public static Mono<ServerResponse> handleException(Throwable throwable) {
        log.error("Exception occurred", throwable);

        if (throwable instanceof ValidationException ex) {
            return validationError(ex.getValidationErrors());
        }

        if (throwable instanceof CustomException ex) {
            return validationError(ex.getMessage(), ex.getErrors());
        }
        // 기타 예외는 시스템 오류로 처리
        return systemError("시스템 오류가 발생했습니다.");
    }
}
