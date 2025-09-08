package dev.gunho.api.ongimemo.v1.handler;

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

    public Mono<ServerResponse> getListPraise(ServerRequest request) {
        log.info("PraiseHandler.getListPraise called");
        return null;
    }
}
