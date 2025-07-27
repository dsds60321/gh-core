package dev.gunho.api.bingous.v1.repository;

import dev.gunho.api.bingous.v1.model.entity.AppSession;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface AppSessionRepository extends ReactiveCrudRepository<AppSession, String> {

    // 세션 키로 활성 세션 조회
    @Query("SELECT * FROM app_sessions WHERE session_key = :sessionKey AND is_active = 1 AND expires_at > :now")
    Mono<AppSession> findActiveSessionByKey(String sessionKey, LocalDateTime now);

    // 사용자의 모든 활성 세션 조회
    @Query("SELECT * FROM app_sessions WHERE user_id = :userId AND is_active = 1 AND expires_at > :now")
    Flux<AppSession> findActiveSessionsByUserId(String userId, LocalDateTime now);

    // 만료된 세션 비활성화
    @Query("UPDATE app_sessions SET is_active = 0 WHERE expires_at <= :now AND is_active = 1")
    Mono<Integer> deactivateExpiredSessions(LocalDateTime now);

    // 세션 키로 세션 조회 (만료 여부 무관)
    @Query("SELECT * FROM app_sessions WHERE session_key = :sessionKey")
    Mono<AppSession> findBySessionKey(String sessionKey);

    @Query("SELECT * FROM app_sessions WHERE user_id = :userId AND device_id = :deviceId AND is_active = 1 AND expires_at > NOW()")
    Mono<AppSession> findActiveSessionByUserAndDevice(String userId, String deviceId);

    @Query("UPDATE app_sessions SET expires_at = :expireDate AND SET last_accessed_at = :lastAccessDate WHERE session_key = :sessionKey")
    Mono<Integer> updateSessionExpiry(String sessionKey, LocalDateTime expireDate, LocalDateTime lastAccessDate);

}
