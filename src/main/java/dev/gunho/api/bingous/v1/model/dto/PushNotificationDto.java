package dev.gunho.api.bingous.v1.model.dto;

import dev.gunho.api.bingous.v1.model.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

public class PushNotificationDto {

    public record Request(
        @NotBlank(message = "대상 사용자 ID는 필수입니다")
        String targetUserId,
        @NotNull(message = "알림 타입은 필수입니다")
        NotificationType type,
        @NotBlank(message = "제목은 필수입니다")
        String title,
        @NotBlank(message = "내용은 필수입니다")
        String body,
        Map<String, String> data
    ){}

    @Builder
    public record Response(
        boolean success,
        String message,
        String messageId
    ){}
}
