package dev.gunho.api.bingous.v1.handler;

import dev.gunho.api.bingous.v1.model.dto.EmailVerify;
import dev.gunho.api.bingous.v1.service.AuthService;
import dev.gunho.api.global.exception.ValidationException;
import dev.gunho.api.global.util.RequestValidator;
import dev.gunho.api.global.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthHandler {

    private final AuthService authService;
    private final RequestValidator requestValidator;

    public Mono<ServerResponse> verifyEmail(ServerRequest request) {
        return request.bodyToMono(EmailVerify.Request.class)
                .flatMap(requestValidator::validate)
                .flatMap(authService::verifyEmail)
                .flatMap(ResponseUtil::ok)
                .onErrorResume(ValidationException.class, ex -> {
                    var errorData = new RequestValidator.ValidationErrorData(
                            ex.getErrorMessages(),
                            ex.getFieldErrors(),
                            ex.getValidationErrors()
                    );
                    return ResponseUtil.badRequest(errorData, "검증 실패");
                })
                .onErrorResume(Exception.class, ex ->
                        ResponseUtil.internalServerError(ex.getMessage(), "처리 중 오류 발생"));
    }
}
