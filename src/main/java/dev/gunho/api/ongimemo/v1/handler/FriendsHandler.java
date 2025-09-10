package dev.gunho.api.ongimemo.v1.handler;

import dev.gunho.api.global.annotation.GhSession;
import dev.gunho.api.global.model.dto.ApiResponse;
import dev.gunho.api.global.model.dto.SessionDto;
import dev.gunho.api.global.util.RequestValidator;
import dev.gunho.api.global.util.ResponseHelper;
import dev.gunho.api.ongimemo.v1.model.dto.FriendsDTO;
import dev.gunho.api.ongimemo.v1.model.dto.PraiseDto;
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
    private final RequestValidator requestValidator;

    public Mono<ServerResponse> getFriends(ServerRequest request) {
        log.info("FriendsHandler.getFriends called");
        return friendsService.getFriends(request).collectList()
                .flatMap(body -> ResponseHelper.ok(ApiResponse.success(body)))
                .onErrorResume(ResponseHelper::handleException);
    }

    public Mono<ServerResponse> inviteFriend(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(FriendsDTO.Request.class)
                .flatMap(requestValidator::validate)
                .flatMap(request -> friendsService.invite(request))
                .flatMap(result -> ResponseHelper.ok(ApiResponse.success(result)))
                .onErrorResume(ResponseHelper::handleException);
    }
}
