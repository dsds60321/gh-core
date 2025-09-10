package dev.gunho.api.ongimemo.v1.handler;

import dev.gunho.api.global.model.dto.ApiResponse;
import dev.gunho.api.global.util.ResponseHelper;
import dev.gunho.api.ongimemo.v1.service.FriendsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendsHandler {

    private final FriendsService friendsService;

    public Mono<ServerResponse> getFriends(ServerRequest request) {
        log.info("FriendsHandler.getFriends called");
        return friendsService.getFriends(request)
                .flatMap(body -> ResponseHelper.ok(ApiResponse.success(body)))
                .onErrorResume(ResponseHelper::handleException);
    }
}
