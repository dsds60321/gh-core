package dev.gunho.api.ongimemo.v1.service;

import dev.gunho.api.global.annotation.GhSession;
import dev.gunho.api.global.constants.CoreConstants;
import dev.gunho.api.global.exception.CustomException;
import dev.gunho.api.global.model.dto.SessionDto;
import dev.gunho.api.global.util.Util;
import dev.gunho.api.ongimemo.v1.model.dto.FriendsDTO;
import dev.gunho.api.ongimemo.v1.repository.FriendsRepository;
import dev.gunho.api.ongimemo.v1.repository.OngiMemoUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendsService {

    private final OngiMemoUserRepository userRepository;
    private final FriendsRepository friendsRepository;

    public Flux<FriendsDTO.Response> getFriends(ServerRequest request) {
        Mono<SessionDto> sessionMono = Mono.deferContextual(ctx ->
                Mono.justOrEmpty(ctx.getOrEmpty(GhSession.class))
        ).cast(SessionDto.class);

        return sessionMono
                .doOnNext(s -> log.info("GhSession in Context => userId={}, sessionKey={}", s.userId(), s.sessionKey()))
                .switchIfEmpty(Mono.error(new IllegalStateException("세션 정보가 없습니다")))
                // 이어서 실제 비즈니스 로직
                .flatMap(s -> {
                    log.info("sessionCalled  {} " , s.sessionKey());
                    return userRepository.findBySessionKey(s.sessionKey());
                })
                .flatMapMany(user -> friendsRepository.findAllWithUserByUserIdx(user.getIdx()));
    }

    public Mono<?> invite(FriendsDTO.Request request) {
        return null;
    }
}
