package dev.gunho.api.bingous.v1.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

public class FcmTokenDto {

    @Getter
    @Builder
    public static class Request {
        @NotBlank(message = "FCM 토큰은 필수입니다")
        private String fcmToken;

        private String deviceId;

        private String platform; // iOS, Android
    }

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String userId;
        private String fcmToken;
        private String deviceId;
        private String platform;
        private Boolean isActive;
    }
}
