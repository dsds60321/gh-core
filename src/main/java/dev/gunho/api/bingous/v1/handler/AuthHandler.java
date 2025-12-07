package dev.gunho.api.bingous.v1.handler;

import dev.gunho.api.bingous.v1.model.dto.EmailVerifyDto;
import dev.gunho.api.bingous.v1.model.dto.SignInDto;
import dev.gunho.api.bingous.v1.model.dto.SignUpDto;
import dev.gunho.api.bingous.v1.service.AuthService;
import dev.gunho.api.bingous.v1.service.SessionService;
import dev.gunho.api.global.constants.CoreConstants;
import dev.gunho.api.global.model.dto.ApiResponse;
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
    private final SessionService sessionService;
    private final RequestValidator requestValidator;

    /**
     * 회원가입 처리
     */
    public Mono<ServerResponse> signUp(ServerRequest request) {
        return request.bodyToMono(SignUpDto.Request.class)
                .flatMap(requestValidator::validate)
                .flatMap(authService::signUp
                )
                .flatMap(serviceResponse -> {

                    ApiResponse<?> response;
                    if (serviceResponse.isSuccess()) {
                        response = ApiResponse.success(serviceResponse);
                    } else {
                        response = ApiResponse.failure(serviceResponse);
                    }

                    return ResponseHelper.ok(response);
                })
                .onErrorResume(ResponseHelper::handleException);
    }

    public Mono<ServerResponse> signIn(ServerRequest request) {
        return request.bodyToMono(SignInDto.Request.class)
                .flatMap(requestValidator::validate)
                .flatMap(signInRequeset ->
                    authService.signIn(signInRequeset, request.exchange().getRequest())
                )
                .flatMap(serviceResponse -> {

                    ApiResponse<?> response;
                    if (serviceResponse.isSuccess()) {
                        response = ApiResponse.success(serviceResponse);
                    } else {
                        response = ApiResponse.failure(serviceResponse);
                    }

                    return ResponseHelper.ok(response);
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

                    return ResponseHelper.ok(response);
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
                .flatMap(verifiedResponse -> {
                    ApiResponse<?> response;
                    if (verifiedResponse.verified()) {
                        response = ApiResponse.success(verifiedResponse);
                    } else {
                        response = ApiResponse.failure(verifiedResponse);
                    }

                    return ResponseHelper.ok(response);
                })
                .onErrorResume(ResponseHelper::handleException);
    }

    public Mono<ServerResponse> signOut(ServerRequest serverRequest) {
        String key = serverRequest.exchange().getRequest().getHeaders().getFirst(CoreConstants.Network.AUTH_KEY);

        return sessionService.invalidateSession(key)
                .then(Mono.defer(() -> {
                    ApiResponse<?> response = ApiResponse.success("로그아웃이 완료되었습니다.");
                    return ResponseHelper.ok(response);
                }))
                .onErrorResume(ResponseHelper::handleException);
    }

    public Mono<ServerResponse> withDraw(ServerRequest serverRequest) {
        String key = serverRequest.exchange().getRequest().getHeaders().getFirst(CoreConstants.Network.AUTH_KEY);

        return authService.withDraw(key)
                .then(Mono.defer(() -> {
                    ApiResponse<?> response = ApiResponse.success("탈퇴가 완료되었습니다.");
                    return ResponseHelper.ok(response);
                }))
                .onErrorResume(ResponseHelper::handleException);
    }

}
