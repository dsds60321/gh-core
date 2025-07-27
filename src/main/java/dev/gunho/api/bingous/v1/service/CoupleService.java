package dev.gunho.api.bingous.v1.service;

import dev.gunho.api.bingous.v1.model.dto.InviteDto;
import dev.gunho.api.bingous.v1.model.entity.InviteToken;
import dev.gunho.api.bingous.v1.repository.AppSessionRepository;
import dev.gunho.api.bingous.v1.repository.InviteTokenRepository;
import dev.gunho.api.bingous.v1.repository.UserRepository;
import dev.gunho.api.global.constants.CoreConstants;
import dev.gunho.api.global.util.RedisUtil;
import dev.gunho.api.global.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static dev.gunho.api.global.constants.CoreConstants.*;
import static dev.gunho.api.global.constants.CoreConstants.Network.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoupleService {

    private final AppSessionRepository appSessionRepository;
    private final InviteTokenRepository inviteTokenRepository;
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;

    /**
     * 상대방에게 링크 요청
     */
    public Mono<String> createInviteLink(ServerHttpRequest request) {
        log.info("createInviteLink - ID : {}", request.getId());

        String sessionKey = request.getHeaders().getFirst(AUTH_KEY);
        log.info("createInviteLink - Session Key: {}", sessionKey);
        return appSessionRepository.findBySessionKey(sessionKey)
                .flatMap(session -> {
                    log.info("createInviteLink - userId : {}  Session Key: {}", session.getUserId(), sessionKey);
                    return userRepository.findById(session.getUserId())
                            .flatMap(user -> {

                                // ID 로 redis에 등록 해당 키로 응답
                                String randomCode = Util.CommonUtil.generateRandomCode(10);
                                log.info("Generated random code: {}", randomCode);

                                // 요청자 ID로 레디스 1일 기간한정 생성
                                return redisUtil.setString(Key.COUPLE_INVITE.formatted(randomCode), user.getId(), Duration.ofDays(1))
                                        .flatMap(isSaved -> {

                                            if (!isSaved) {
                                                log.error("Redis save failed - ID: {}", user.getId());
                                                return Mono.empty();
                                            }

                                            // 레디스 성공 커플 요청 저장
                                            InviteToken token = InviteToken.toEntity(randomCode, user.getId());
                                            return inviteTokenRepository.save(token)
                                                    .map(inviteToken -> Host.INVITE_URL.formatted(randomCode));
                                        });
                            });
                })
                .switchIfEmpty(Mono.empty())
                .onErrorResume(error -> {
                    log.error("Error in createInviteLink - ID: {}", request.getId(), error);
                    return Mono.empty();
                });
    }
}
