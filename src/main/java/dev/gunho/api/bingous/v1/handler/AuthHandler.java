package dev.gunho.api.bingous.v1.handler;

import dev.gunho.api.bingous.v1.model.dto.EmailVerify;
import dev.gunho.api.bingous.v1.model.dto.SignUp;
import dev.gunho.api.bingous.v1.service.AuthService;
import dev.gunho.api.bingous.v1.service.UserService;
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

    public Mono<ServerResponse> signUp(ServerRequest request) {
        return request.bodyToMono(SignUp.Request.class)
                .flatMap(requestValidator::validate)
                .flatMap(signUpRequest -> {
                    // ServerRequest에서 ServerHttpRequest 추출
                    return authService.signUp(signUpRequest, request.exchange().getRequest());
                })

                .flatMap(result -> {
                    if (result.isSuccess()) {
                        return ResponseUtil.ok(result.getData());
                    } else {
                        return ResponseUtil.badRequest(result.getMessage(), result.getCode());
                    }
                })
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

    public Mono<ServerResponse> confirmEamil(ServerRequest request) {
        return request.bodyToMono(EmailVerify.VerifyCodeRequest.class)
                .flatMap(requestValidator::validate)
                .flatMap(authService::confirmEmail)
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
