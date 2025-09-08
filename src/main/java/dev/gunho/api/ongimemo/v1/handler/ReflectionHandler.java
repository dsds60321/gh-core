package dev.gunho.api.ongimemo.v1.handler;

import dev.gunho.api.global.model.dto.ApiResponse;
import dev.gunho.api.global.util.ResponseHelper;
import dev.gunho.api.ongimemo.v1.service.ReflectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReflectionHandler {

    private final ReflectionService reflectionService;

    public Mono<ServerResponse> getAllByCreatedAt(ServerRequest serverRequest) {
        return reflectionService.search(serverRequest)
                .collectList()
                .flatMap(result -> ResponseHelper.ok(ApiResponse.success(result)))
                .onErrorResume(ResponseHelper::handleException);
    }
}
