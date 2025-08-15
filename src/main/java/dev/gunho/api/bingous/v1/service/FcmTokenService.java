package dev.gunho.api.bingous.v1.service;

import dev.gunho.api.bingous.v1.model.entity.FcmToken;
import dev.gunho.api.bingous.v1.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;

    /**
     * FCM 토큰 저장/업데이트
     */
    public Mono<FcmToken> saveOrUpdateFcmToken(String userId, String fcmToken, String deviceId, String platform) {
        return fcmTokenRepository.findByUserIdAndDeviceId(userId, deviceId)
                .flatMap(existingToken -> {
                    // 기존 토큰 업데이트
                    existingToken.setFcmToken(fcmToken);
                    existingToken.setPlatform(platform);
                    existingToken.setIsActive(true);
                    existingToken.setUpdatedAt(LocalDateTime.now());
                    return fcmTokenRepository.save(existingToken);
                })
                .switchIfEmpty(
                        // 새 토큰 생성
                        fcmTokenRepository.save(FcmToken.builder()
                                .userId(userId)
                                .fcmToken(fcmToken)
                                .deviceId(deviceId)
                                .platform(platform)
                                .isActive(true)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build())
                )
                .doOnSuccess(token -> log.info("FCM 토큰 저장 완료. userId: {}, deviceId: {}", userId, deviceId))
                .doOnError(error -> log.error("FCM 토큰 저장 실패. userId: {}, deviceId: {}", userId, deviceId, error));
    }

    /**
     * 사용자의 활성 FCM 토큰들 조회
     */
    public Flux<FcmToken> getActiveFcmTokens(String userId) {
        return fcmTokenRepository.findActiveTokensByUserId(userId)
                .doOnNext(token -> log.debug("활성 FCM 토큰 조회. userId: {}, deviceId: {}", userId, token.getDeviceId()));
    }

    /**
     * 사용자의 가장 최근 FCM 토큰 조회
     */
    public Mono<FcmToken> getLatestFcmToken(String userId) {
        return fcmTokenRepository.findLatestActiveTokenByUserId(userId)
                .doOnNext(token -> log.debug("최근 FCM 토큰 조회. userId: {}, deviceId: {}", userId, token.getDeviceId()));
    }

    /**
     * FCM 토큰 비활성화
     */
    public Mono<Void> deactivateFcmToken(String fcmToken) {
        return fcmTokenRepository.deactivateTokenByFcmToken(fcmToken)
                .doOnSuccess(count -> log.info("FCM 토큰 비활성화 완료. 영향받은 행: {}", count))
                .then();
    }

    /**
     * 사용자의 모든 FCM 토큰 비활성화
     */
    public Mono<Void> deactivateAllUserTokens(String userId) {
        return fcmTokenRepository.deactivateAllTokensByUserId(userId)
                .doOnSuccess(count -> log.info("사용자 모든 FCM 토큰 비활성화 완료. userId: {}, 영향받은 행: {}", userId, count))
                .then();
    }
}
