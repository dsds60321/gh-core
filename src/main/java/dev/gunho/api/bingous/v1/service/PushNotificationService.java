package dev.gunho.api.bingous.v1.service;

import com.google.firebase.messaging.*;
import dev.gunho.api.bingous.v1.model.dto.PushNotificationDto;
import dev.gunho.api.global.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import dev.gunho.api.bingous.v1.model.entity.FcmToken;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationService {

    private final FcmTokenService fcmTokenService;

    /**
     * Push 알림 전송
     */
    public Mono<PushNotificationDto.Response> sendPushNotification(PushNotificationDto.Request request) {
        return fcmTokenService.getActiveFcmTokens(request.targetUserId())
                .collectList()
                .flatMap(tokens -> {
                    if (tokens.isEmpty()) {
                        log.warn("활성 FCM 토큰이 없습니다. userId: {}", request.targetUserId());
                        return Mono.just(PushNotificationDto.Response.builder()
                                .success(false)
                                .message("사용자의 활성 FCM 토큰이 존재하지 않습니다.")
                                .build());
                    }

                    // 모든 활성 토큰에 알림 전송
                    return Flux.fromIterable(tokens)
                            .flatMap(token -> sendToFirebase(token, request))
                            .collectList()
                            .map(responses -> {
                                long successCount = responses.stream()
                                        .mapToLong(response -> response.success() ? 1 : 0)
                                        .sum();

                                return PushNotificationDto.Response.builder()
                                        .success(successCount > 0)
                                        .message(String.format("총 %d개 디바이스 중 %d개에 알림 전송 성공",
                                                tokens.size(), successCount))
                                        .build();
                            });
                })
                .onErrorResume(error -> {
                    log.error("Push 알림 전송 실패", error);
                    return Mono.just(PushNotificationDto.Response.builder()
                            .success(false)
                            .message("알림 전송 중 오류가 발생했습니다: " + error.getMessage())
                            .build());
                });
    }

    /**
     * FCM 토큰 저장/업데이트
     */
    public Mono<FcmToken> saveOrUpdateFcmToken(String userId, String fcmToken, String deviceId, String platform) {
        return fcmTokenService.saveOrUpdateFcmToken(userId, fcmToken, deviceId, platform);
    }

    /**
     * 사용자의 활성 FCM 토큰들 조회
     */
    public Flux<FcmToken> getActiveFcmTokens(String userId) {
        return fcmTokenService.getActiveFcmTokens(userId);
    }

    /**
     * 사용자의 모든 FCM 토큰 비활성화
     */
    public Mono<Void> deactivateAllUserTokens(String userId) {
        return fcmTokenService.deactivateAllUserTokens(userId);
    }

    /**
     * FCM 토큰 비활성화
     */
    public Mono<Void> deactivateFcmToken(String fcmToken) {
        return fcmTokenService.deactivateFcmToken(fcmToken);
    }

    /**
     * Firebase로 실제 메시지 전송
     */
    private Mono<PushNotificationDto.Response> sendToFirebase(FcmToken fcmToken, PushNotificationDto.Request request) {
        return Mono.fromCallable(() -> {
            try {
                // FCM 메시지 빌드
                Message.Builder messageBuilder = Message.builder()
                        .setToken(fcmToken.getFcmToken())
                        .setNotification(Notification.builder()
                                .setTitle(request.title())
                                .setBody(request.body())
                                .build());

                // 플랫폼별 설정
                if ("iOS".equalsIgnoreCase(fcmToken.getPlatform())) {
                    messageBuilder.setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setSound("default")
                                    .setBadge(1)
                                    .build())
                            .build());
                } else if ("Android".equalsIgnoreCase(fcmToken.getPlatform())) {
                    messageBuilder.setAndroidConfig(AndroidConfig.builder()
                            .setNotification(AndroidNotification.builder()
                                    .setSound("default")
                                    .setChannelId("default")
                                    .build())
                            .build());
                }

                // 데이터 추가
                if (Util.CommonUtil.isNotEmpty(request.data())) {
                    messageBuilder.putAllData(request.data());
                }

                // 알림 타입 추가
                messageBuilder.putData("type", request.type().getValue());

                Message message = messageBuilder.build();

                // FCM 전송
                String messageId = FirebaseMessaging.getInstance().send(message);

                log.info("Push 알림 전송 성공. MessageId: {}, UserId: {}, DeviceId: {}",
                        messageId, fcmToken.getUserId(), fcmToken.getDeviceId());

                return PushNotificationDto.Response.builder()
                        .success(true)
                        .message("알림이 성공적으로 전송되었습니다.")
                        .messageId(messageId)
                        .build();

            } catch (FirebaseMessagingException e) {
                log.error("Firebase 메시지 전송 실패. DeviceId: {}", fcmToken.getDeviceId(), e);

                // 토큰이 유효하지 않은 경우 처리
                if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED ||
                        e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT) {
                    // FCM 토큰 비활성화 처리
                    fcmTokenService.deactivateFcmToken(fcmToken.getFcmToken()).subscribe();
                }

                return PushNotificationDto.Response.builder()
                        .success(false)
                        .message("Firebase 메시지 전송 실패: " + e.getMessage())
                        .build();
            }
        });
    }
}

