package dev.gunho.api.bingous.v1.repository;

import dev.gunho.api.bingous.v1.model.entity.FcmToken;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface FcmTokenRepository extends ReactiveCrudRepository<FcmToken, Long> {

    /**
     * 사용자의 활성 FCM 토큰 조회
     */
    @Query("SELECT * FROM fcm_tokens WHERE user_id = :userId AND is_active = true ORDER BY updated_at DESC")
    Flux<FcmToken> findActiveTokensByUserId(String userId);

    /**
     * 사용자의 가장 최근 FCM 토큰 조회
     */
    @Query("SELECT * FROM fcm_tokens WHERE user_id = :userId AND is_active = true ORDER BY updated_at DESC LIMIT 1")
    Mono<FcmToken> findLatestActiveTokenByUserId(String userId);

    /**
     * 특정 사용자와 디바이스의 FCM 토큰 조회
     */
    @Query("SELECT * FROM fcm_tokens WHERE user_id = :userId AND device_id = :deviceId AND is_active = true")
    Mono<FcmToken> findByUserIdAndDeviceId(String userId, String deviceId);

    /**
     * FCM 토큰으로 조회
     */
    @Query("SELECT * FROM fcm_tokens WHERE fcm_token = :fcmToken AND is_active = true")
    Mono<FcmToken> findByFcmToken(String fcmToken);

    /**
     * 사용자의 모든 토큰 비활성화
     */
    @Query("UPDATE fcm_tokens SET is_active = false, updated_at = CURRENT_TIMESTAMP WHERE user_id = :userId")
    Mono<Integer> deactivateAllTokensByUserId(String userId);

    /**
     * 특정 토큰 비활성화
     */
    @Query("UPDATE fcm_tokens SET is_active = false, updated_at = CURRENT_TIMESTAMP WHERE fcm_token = :fcmToken")
    Mono<Integer> deactivateTokenByFcmToken(String fcmToken);
}
