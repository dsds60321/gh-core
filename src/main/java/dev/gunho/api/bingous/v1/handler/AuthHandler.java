package dev.gunho.api.bingous.v1.handler;

import dev.gunho.api.bingous.v1.model.dto.EmailVerify;
import dev.gunho.api.bingous.v1.model.dto.SignIn;
import dev.gunho.api.bingous.v1.model.dto.SignUp;
import dev.gunho.api.bingous.v1.service.AuthService;
import dev.gunho.api.bingous.v1.service.AuthServiceV2;
import dev.gunho.api.global.model.dto.ApiResponse;
import dev.gunho.api.global.util.RequestValidator;
import dev.gunho.api.global.util.ResponseHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthHandler {

    private final AuthService authService;
    private final AuthServiceV2 authServiceV2;
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

    public Mono<ServerResponse> signIn(ServerRequest request) {
        return request.bodyToMono(SignIn.Request.class)
                .flatMap(requestValidator::validate)
                .flatMap(signInRequeset ->
                    authService.signIn(signInRequeset, request.exchange().getRequest())
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
                .flatMap(authServiceV2::sendEmailVerifyCode)
                .flatMap(success -> {
                    ApiResponse<?> response;
                    if (success) {
                        response = ApiResponse.success(null, "인증 코드가 전송되었습니다.");
                    } else {
                        response = ApiResponse.failure("인증 코드 전송에 실패했습니다.", "EMAIL_SEND_FAILED");
                    }
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                })
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
