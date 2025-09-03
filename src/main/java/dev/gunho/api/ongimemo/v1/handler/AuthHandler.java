package dev.gunho.api.ongimemo.v1.handler;

import dev.gunho.api.global.model.dto.ApiResponse;
import dev.gunho.api.global.util.RequestValidator;
import dev.gunho.api.global.util.ResponseHelper;
import dev.gunho.api.ongimemo.v1.model.dto.SignUpCompleteDTO;
import dev.gunho.api.ongimemo.v1.model.dto.SignUpDTO;
import dev.gunho.api.ongimemo.v1.service.AuthService;
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

    public Mono<ServerResponse> signUpStep1(ServerRequest request) {
        log.info("AuthHandler.signUpStep1 called");
        return request.bodyToMono(SignUpDTO.Request.class)
                .flatMap(requestValidator::validate)
                .flatMap(authService::requestSignUp)
                .flatMap(body -> ResponseHelper.ok(ApiResponse.success(body)))
                .onErrorResume(ResponseHelper::handleException);
    }

    public Mono<ServerResponse> signUpStep2(ServerRequest request) {
        log.info("AuthHandler.signUpStep2 called");
        return request.bodyToMono(SignUpCompleteDTO.Request.class)
                .flatMap(requestValidator::validate)
                .flatMap(obj ->
                    authService.signUpComplete(request.exchange().getRequest(), obj)
                )
                .flatMap(body -> ResponseHelper.ok(ApiResponse.success(body)))
                .onErrorResume(ResponseHelper::handleException);
    }
}
