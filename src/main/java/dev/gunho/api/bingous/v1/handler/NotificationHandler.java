package dev.gunho.api.bingous.v1.handler;


import dev.gunho.api.bingous.v1.model.dto.FcmTokenDto;
import dev.gunho.api.bingous.v1.model.dto.PushNotificationDto;
import dev.gunho.api.bingous.v1.service.PushNotificationService;
import dev.gunho.api.global.model.dto.ApiResponse;
import dev.gunho.api.global.util.RequestValidator;
import dev.gunho.api.global.util.ResponseHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationHandler {

    private final RequestValidator requestValidator;
    private final PushNotificationService pushNotificationService;

    /**
     * Push 알림 전송
     */
    public Mono<ServerResponse> sendPushNotification(ServerRequest request) {
        String currentUserId = (String) request.exchange().getAttributes().get("userId");

        return request.bodyToMono(PushNotificationDto.Request.class)
                .flatMap(requestValidator::validate)
                .flatMap(pushRequest -> {
                    log.info("Push 알림 전송 요청. 요청자: {}, 대상: {}, 타입: {}",
                            currentUserId, pushRequest.targetUserId(), pushRequest.type());
                    return pushNotificationService.sendPushNotification(pushRequest);
                })
                .flatMap(response -> {
                    if (response.success()) {
                        ApiResponse<?> apiResponse = ApiResponse.success(response, "알림이 전송되었습니다.");
                        return ResponseHelper.ok(apiResponse);
                    } else {
                        ApiResponse<?> apiResponse = ApiResponse.failure(response.message());
                        return ResponseHelper.badRequest(apiResponse);
                    }
                })
                .onErrorResume(ResponseHelper::handleException);
    }

    /**
     * FCM 토큰 등록/업데이트
     */
    public Mono<ServerResponse> registerFcmToken(ServerRequest request) {
        String userId = (String) request.exchange().getAttributes().get("userId");

        return request.bodyToMono(FcmTokenDto.Request.class)
                .flatMap(requestValidator::validate)
                .flatMap(fcmRequest -> {
                    log.info("FCM 토큰 등록 요청. userId: {}, deviceId: {}", userId, fcmRequest.getDeviceId());
                    return pushNotificationService.saveOrUpdateFcmToken(
                            userId,
                            fcmRequest.getFcmToken(),
                            fcmRequest.getDeviceId(),
                            fcmRequest.getPlatform()
                    );
                })
                .flatMap(savedToken -> {
                    ApiResponse<?> response = ApiResponse.success(savedToken, "FCM 토큰이 등록되었습니다.");
                    return ResponseHelper.ok(response);
                })
                .onErrorResume(ResponseHelper::handleException);
    }

    /**
     * 사용자의 모든 FCM 토큰 비활성화 (로그아웃 시 사용)
     */
    public Mono<ServerResponse> deactivateAllTokens(ServerRequest request) {
        String userId = (String) request.exchange().getAttributes().get("userId");

        return pushNotificationService.deactivateAllUserTokens(userId)
                .then(Mono.fromCallable(() -> ApiResponse.success("모든 FCM 토큰이 비활성화되었습니다.")))
                .flatMap(ResponseHelper::ok)
                .onErrorResume(ResponseHelper::handleException);
    }

    /**
     * 사용자의 활성 FCM 토큰 조회
     */
    public Mono<ServerResponse> getActiveTokens(ServerRequest request) {
        String userId = (String) request.exchange().getAttributes().get("userId");

        return pushNotificationService.getActiveFcmTokens(userId)
                .collectList()
                .flatMap(tokens -> {
                    ApiResponse<?> response = ApiResponse.success(tokens, "활성 FCM 토큰 조회 완료");
                    return ResponseHelper.ok(response);
                })
                .onErrorResume(ResponseHelper::handleException);
    }
}
