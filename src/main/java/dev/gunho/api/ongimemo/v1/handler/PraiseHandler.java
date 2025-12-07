package dev.gunho.api.ongimemo.v1.handler;

import dev.gunho.api.global.model.dto.ApiResponse;
import dev.gunho.api.global.util.RequestValidator;
import dev.gunho.api.global.util.ResponseHelper;
import dev.gunho.api.ongimemo.v1.model.dto.PraiseDto;
import dev.gunho.api.ongimemo.v1.model.dto.ReflectionDTO;
import dev.gunho.api.ongimemo.v1.service.PraiseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class PraiseHandler {

    private final PraiseService praiseService;
    private final RequestValidator requestValidator;

    public Mono<ServerResponse> getAllByCreatedAt(ServerRequest serverRequest) {
        return praiseService.search(serverRequest)
                .collectList()
                .flatMap(result -> ResponseHelper.ok(ApiResponse.success(result)))
                .onErrorResume(ResponseHelper::handleException);
    }

    public Mono<ServerResponse> createPraise(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(PraiseDto.Request.class)
                .flatMap(requestValidator::validate)
                .flatMap(request -> praiseService.create(request))
                .flatMap(result -> ResponseHelper.ok(ApiResponse.success(result)))
                .onErrorResume(ResponseHelper::handleException);
    }
}
