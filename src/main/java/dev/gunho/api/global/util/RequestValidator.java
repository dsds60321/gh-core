package dev.gunho.api.global.util;

import dev.gunho.api.global.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestValidator {

    private final Validator validator;

    /**
     * 1. 가장 간단한 검증 메서드
     */
    public <T> Mono<T> validate(T object) {
        return validate(object, new Class<?>[0]);
    }

    /**
     * 2. 검증 그룹을 지원하는 메서드
     */
    public <T> Mono<T> validate(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validator.validate(object, groups);

        if (violations.isEmpty()) {
            log.debug("Validation passed for {}", object.getClass().getSimpleName());
            return Mono.just(object);
        }

        List<ValidationException.ValidationError> errors = violations.stream()
                .map(violation -> new ValidationException.ValidationError(
                        violation.getPropertyPath().toString(),
                        violation.getMessage(),
                        violation.getInvalidValue(),
                        violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName()
                ))
                .toList();

        log.warn("Validation failed for {} with {} errors",
                object.getClass().getSimpleName(), errors.size());

        return Mono.error(new ValidationException("입력값 검증에 실패했습니다.", errors));
    }

    /**
     * 3. 요청을 파싱하고 검증한 후 처리하는 헬퍼 메서드 (추천!)
     */
    public <T, R> Function<ServerRequest, Mono<ServerResponse>> validateAndProcess(
            Class<T> dtoClass,
            Function<T, Mono<R>> processor) {

        return request -> request.bodyToMono(dtoClass)
                .doOnNext(dto -> log.debug("Processing request: {}", dto.getClass().getSimpleName()))
                .flatMap(this::validate)
                .flatMap(processor)
                .flatMap(ResponseUtil::ok)
                .doOnError(error -> log.error("Request processing failed", error))
                .onErrorResume(ValidationException.class, this::handleValidationError)
                .onErrorResume(Exception.class, this::handleGenericError);
    }

    /**
     * 4. 검증 그룹과 함께 사용하는 헬퍼 메서드 (가장 강력!)
     */
    public <T, R> Function<ServerRequest, Mono<ServerResponse>> validateAndProcess(
            Class<T> dtoClass,
            Function<T, Mono<R>> processor,
            Class<?>... validationGroups) {

        return request -> request.bodyToMono(dtoClass)
                .doOnNext(dto -> log.debug("Processing request with validation groups: {}",
                        java.util.Arrays.toString(validationGroups)))
                .flatMap(dto -> validate(dto, validationGroups))
                .flatMap(processor)
                .flatMap(ResponseUtil::ok)
                .doOnError(error -> log.error("Request processing failed", error))
                .onErrorResume(ValidationException.class, this::handleValidationError)
                .onErrorResume(Exception.class, this::handleGenericError);
    }

    /**
     * 5. 커스텀 검증 로직을 추가할 수 있는 메서드
     */
    public <T, R> Function<ServerRequest, Mono<ServerResponse>> validateAndProcessWithCustom(
            Class<T> dtoClass,
            Function<T, Mono<T>> customValidator,
            Function<T, Mono<R>> processor,
            Class<?>... validationGroups) {

        return request -> request.bodyToMono(dtoClass)
                .flatMap(dto -> validate(dto, validationGroups))
                .flatMap(customValidator) // 커스텀 검증 로직
                .flatMap(processor)
                .flatMap(ResponseUtil::ok)
                .onErrorResume(ValidationException.class, this::handleValidationError)
                .onErrorResume(Exception.class, this::handleGenericError);
    }

    /**
     * ValidationException 처리 - 개선된 ResponseUtil 사용
     */
    private Mono<ServerResponse> handleValidationError(ValidationException ex) {
        log.warn("Validation error occurred: {}", ex.getErrorMessages());

        // 검증 오류 정보를 포함한 응답 데이터 생성
        ValidationErrorData errorData = new ValidationErrorData(
                ex.getErrorMessages(),
                ex.getFieldErrors(),
                ex.getValidationErrors()
        );

        return ResponseUtil.validationError(errorData, "입력값 검증에 실패했습니다. 입력값을 확인해주세요.");
    }

    /**
     * 일반 Exception 처리 - 개선된 ResponseUtil 사용
     */
    private Mono<ServerResponse> handleGenericError(Exception ex) {
        log.error("Unexpected error occurred", ex);

        return ResponseUtil.internalServerError(ex.getMessage(), "서버 내부 오류가 발생했습니다.");
    }

    /**
     * 검증 오류 데이터를 담는 클래스
     */
    public record ValidationErrorData(
            List<String> messages,
            java.util.Map<String, String> fieldErrors,
            List<ValidationException.ValidationError> details
    ) {}
}
