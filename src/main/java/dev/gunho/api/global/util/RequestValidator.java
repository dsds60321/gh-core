package dev.gunho.api.global.util;

import dev.gunho.api.global.exception.ValidationException;
import dev.gunho.api.global.model.dto.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestValidator {

    private final Validator validator;

    /**
     * 요청 객체를 검증하고 유효한 경우 반환
     */
    public <T> Mono<T> validate(T request) {
        if (request == null) {
            return Mono.error(new ValidationException("요청 객체가 null입니다."));
        }

        Set<ConstraintViolation<T>> violations = validator.validate(request);

        if (violations.isEmpty()) {
            return Mono.just(request);
        }

        Map<String, String> errorMap = new HashMap<>();
        for (ConstraintViolation<T> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errorMap.put(fieldName, errorMessage);
        }

        return Mono.error(new ValidationException("입력값 검증에 실패했습니다.", errorMap));
    }

    /**
     * 검증 결과를 ServerResponse로 변환
     */
    public <T> Mono<ServerResponse> validateAndRespond(T request) {
        return validate(request)
                .flatMap(validRequest -> ResponseHelper.toServerResponse(
                        Mono.just(ServiceResult.success(validRequest))))
                .onErrorResume(this::handleValidationError);
    }

    /**
     * 검증 오류 처리
     */
    private Mono<ServerResponse> handleValidationError(Throwable error) {
        if (error instanceof ValidationException validationException) {
            return ResponseHelper.validationError(validationException.getValidationErrors());
        }

        log.error("Unexpected error during validation", error);
        return ResponseHelper.systemError("검증 중 오류가 발생했습니다.");
    }
}
