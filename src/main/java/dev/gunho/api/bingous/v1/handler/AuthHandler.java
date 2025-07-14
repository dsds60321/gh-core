package dev.gunho.api.bingous.v1.handler;

import dev.gunho.api.bingous.v1.model.dto.EmailVerify;
import dev.gunho.api.bingous.v1.model.dto.SignUp;
import dev.gunho.api.bingous.v1.service.AuthService;
import dev.gunho.api.global.util.RequestValidator;
import dev.gunho.api.global.util.ResponseHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthHandler {

    private final AuthService authService;
    private final RequestValidator requestValidator;

    /**
     * 회원가입 처리
     */
    public Mono<ServerResponse> signUp(ServerRequest request) {
        return request.bodyToMono(SignUp.Request.class)
                .flatMap(requestValidator::validate)
                .flatMap(signUpRequest ->
                        authService.signUp(signUpRequest, request.exchange().getRequest())
                )
                .flatMap(ResponseHelper::toServerResponse)
                .onErrorResume(ResponseHelper::handleException);
    }

    /**
     * 이메일 인증 코드 전송
     */
    public Mono<ServerResponse> verifyEmail(ServerRequest request) {
        return request.bodyToMono(EmailVerify.Request.class)
                .flatMap(requestValidator::validate)
                .flatMap(authService::verifyEmail)
                .flatMap(ResponseHelper::toServerResponse)
                .onErrorResume(ResponseHelper::handleException);
    }

    /**
     * 이메일 인증 코드 확인
     */
    public Mono<ServerResponse> confirmEmail(ServerRequest request) {
        return request.bodyToMono(EmailVerify.VerifyCodeRequest.class)
                .flatMap(requestValidator::validate)
                .flatMap(authService::confirmEmail)
                .flatMap(ResponseHelper::toServerResponse)
                .onErrorResume(ResponseHelper::handleException);
    }
}
