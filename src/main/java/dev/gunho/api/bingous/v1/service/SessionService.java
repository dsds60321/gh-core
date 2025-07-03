package dev.gunho.api.bingous.v1.service;

import dev.gunho.api.bingous.v1.model.entity.AppSession;
import dev.gunho.api.bingous.v1.repository.AppSessionRepository;
import dev.gunho.api.bingous.v1.util.DeviceInfoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final AppSessionRepository sessionRepository;
    private static final int SESSION_DURATION_DAYS = 30; // 30일 세션 유지

    /**
     * 새로운 세션 생성
     */
    public Mono<AppSession> createSession(String userId, ServerHttpRequest request) {
        DeviceInfoUtil deviceInfo = DeviceInfoUtil.extractFromRequest(request);
        String deviceId = request.getHeaders().getFirst("X-Device-ID");

        // 기존 동일 디바이스 세션이 있다면 비활성화
        Mono<Void> deactivateExisting = deviceId != null ?
                sessionRepository.findActiveSessionByUserAndDevice(userId, deviceId)
                        .flatMap(existingSession -> {
                            log.info("Deactivating existing session for user {} on device {}", userId, deviceId);
                            return sessionRepository.save(existingSession.withDeactivated());
                        })
                        .then() :
                Mono.empty();

        return deactivateExisting.then(Mono.defer(() -> {
            AppSession session = AppSession.builder()
                    .id(null) // Auto increment - null이므로 자동으로 INSERT
                    .sessionKey(generateSessionKey())
                    .userId(userId)
                    .deviceId(deviceId)
                    .deviceType(deviceInfo.getDeviceType())
                    .deviceName(deviceInfo.getDeviceName())
                    .ipAddress(deviceInfo.getIpAddress())
                    .userAgent(deviceInfo.getUserAgent())
                    .isActive(true)
                    .expiresAt(LocalDateTime.now().plusDays(SESSION_DURATION_DAYS))
                    .lastAccessedAt(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .build();

            return sessionRepository.save(session)
                    .doOnSuccess(savedSession ->
                            log.info("Session created - ID: {}, User: {}, SessionKey: {}, DeviceType: {}",
                                    savedSession.getId(), userId, savedSession.getSessionKey(), deviceInfo.getDeviceType()));
        }));
    }

    /**
     * 세션 키로 유효한 세션 조회 및 접근 시간 업데이트
     */
    public Mono<AppSession> validateSession(String sessionKey) {
        return sessionRepository.findActiveSessionByKey(sessionKey, LocalDateTime.now())
                .flatMap(session -> {
                    // 마지막 접근 시간 업데이트 (ID가 있으므로 자동으로 UPDATE)
                    return sessionRepository.save(session.withUpdatedLastAccessed());
                })
                .doOnNext(session -> log.debug("Session validated - ID: {}, User: {}, SessionKey: {}",
                        session.getId(), session.getUserId(), sessionKey))
                .doOnError(error -> log.warn("Session validation failed - SessionKey: {}", sessionKey));
    }

    /**
     * 세션 무효화 (로그아웃)
     */
    public Mono<Void> invalidateSession(String sessionKey) {
        return sessionRepository.findBySessionKey(sessionKey)
                .flatMap(session -> sessionRepository.save(session.withDeactivated()))
                .doOnSuccess(session -> log.info("Session invalidated - ID: {}, SessionKey: {}",
                        session.getId(), sessionKey))
                .then();
    }

    /**
     * 사용자의 모든 세션 무효화
     */
    public Mono<Void> invalidateAllUserSessions(String userId) {
        return sessionRepository.findActiveSessionsByUserId(userId, LocalDateTime.now())
                .flatMap(session -> sessionRepository.save(session.withDeactivated()))
                .then()
                .doOnSuccess(unused -> log.info("All sessions invalidated for user: {}", userId));
    }

    /**
     * 만료된 세션 정리 (스케줄러에서 호출)
     */
    public Mono<Integer> cleanupExpiredSessions() {
        return sessionRepository.deactivateExpiredSessions(LocalDateTime.now())
                .doOnSuccess(count -> log.info("Cleaned up {} expired sessions", count));
    }

    /**
     * 보안 세션 키 생성
     */
    private String generateSessionKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32]; // 256 bits
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
