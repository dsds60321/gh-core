package dev.gunho.api.bingous.v1.handler;

import dev.gunho.api.bingous.v1.model.dto.EmailVerifyDto;
import dev.gunho.api.bingous.v1.model.dto.SignInDto;
import dev.gunho.api.bingous.v1.model.dto.SignUpDto;
import dev.gunho.api.bingous.v1.service.AuthService;
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
    private final RequestValidator requestValidator;

    /**
     * 회원가입 처리
     */
    public Mono<ServerResponse> signUp(ServerRequest request) {
        return request.bodyToMono(SignUpDto.Request.class)
                .flatMap(requestValidator::validate)
                .flatMap(authService::signUp
                )
                .flatMap(response -> {
                    if (response.isSuccess()) {
                        return ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ApiResponse.success(response));
                    } else {
                        return ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ApiResponse.failure(response));
                    }
                })
                .onErrorResume(ResponseHelper::handleException);
    }

    public Mono<ServerResponse> signIn(ServerRequest request) {
        return request.bodyToMono(SignInDto.Request.class)
                .flatMap(requestValidator::validate)
                .flatMap(signInRequeset ->
                    authService.signIn(signInRequeset, request.exchange().getRequest())
                )
                .flatMap(response -> {
                    if (response.isSuccess()) {
                        return ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ApiResponse.success(response));
                    } else {
                        return ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ApiResponse.failure(response));
                    }
                })
                .onErrorResume(ResponseHelper::handleException);
    }

    /**
     * 이메일 인증 코드 전송
     */
    public Mono<ServerResponse> verifyEmail(ServerRequest request) {
        return request.bodyToMono(EmailVerifyDto.Request.class)
                .flatMap(requestValidator::validate)
                .flatMap(authService::sendEmailVerifyCode)
                .flatMap(success -> {
                    ApiResponse<?> response;
                    if (success) {
                        response = ApiResponse.success( "인증 코드가 전송되었습니다.");
                    } else {
                        response = ApiResponse.failure("인증 코드 전송에 실패했습니다.");
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
        return request.bodyToMono(EmailVerifyDto.VerifyCodeRequest.class)
                .flatMap(requestValidator::validate)
                .flatMap(authService::verifyEmailCode)
                .flatMap(response -> {
                    if (response.verified()) {
                        return ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response);
                    } else {
                        return ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response);
                    }
                })
                .onErrorResume(ResponseHelper::handleException);
    }
}
