package dev.gunho.api.bingous.v1.service;

import dev.gunho.api.bingous.v1.model.dto.InviteDto;
import dev.gunho.api.bingous.v1.repository.AppSessionRepository;
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

import static dev.gunho.api.global.constants.CoreConstants.Network.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoupleService {

    private final AppSessionRepository appSessionRepository;
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;

    /**
     * 상대방에게 링크 요청
     */
    public Mono<String> createInviteLink(ServerHttpRequest request) {
        log.info("createInviteLink - ID : {}", request.getId());

        String sessionKey = request.getHeaders().getFirst(AUTH_KEY);
        return appSessionRepository.findBySessionKey(sessionKey)
                .flatMap(session -> {
                    return userRepository.findById(session.getUserId())
                            .flatMap(user -> {

                                // ID 로 redis에 등록 해당 키로 응답
                                String randomCode = Util.CommonUtil.generateRandomCode(10);

                                // 요청자 ID로 레디스 1일 기간한정 생성
                                return redisUtil.setString(CoreConstants.Key.COUPLE_INVITE.formatted(randomCode), user.getId(), Duration.ofDays(1))
                                        .flatMap(isSaved -> {

                                            if (!isSaved) {
                                                log.error("Redis save failed - ID: {}", user.getId());
                                                return Mono.just(null);
                                            }

                                            // 레디스 성공 커플 요청 저장


                                            return Mono.just(randomCode);
                                        });


                            });
                })
                .switchIfEmpty(Mono.just(null))
                .onErrorResume(error -> {
                    log.error("Error in inviteCouple - ID: {}", request.getId(), error);
                    return Mono.just(null);
                });
    }
}
