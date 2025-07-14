package dev.gunho.api.global.util;

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
     * ServiceResult를 ApiResponse로 변환하여 ServerResponse 생성
     */
    public static <T> Mono<ServerResponse> toServerResponse(ServiceResult<T> result) {
        if (result.isSuccess()) {
            return ServerResponse
                    .status(result.getResponseCode().getHttpStatus())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(ApiResponse.success(result.getData(), result.getMessage()));
        } else {
            return ServerResponse
                    .status(result.getResponseCode().getHttpStatus())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(ApiResponse.failure(result.getMessage(), result.getResponseCode().getCode()));
        }
    }

    /**
     * ServiceResult Mono를 ServerResponse Mono로 변환
     */
    public static <T> Mono<ServerResponse> toServerResponse(Mono<ServiceResult<T>> resultMono) {
        return resultMono.flatMap(ResponseHelper::toServerResponse);
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

        // 기타 예외는 시스템 오류로 처리
        return systemError("시스템 오류가 발생했습니다.");
    }
}
