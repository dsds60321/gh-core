package dev.gunho.api.bingous.v1.model.dto;

import dev.gunho.api.bingous.v1.model.entity.Couples;
import dev.gunho.api.bingous.v1.model.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SignInDto {

    public record Request(
            @NotBlank(message = "ID는 필수입니다")
            String id,
            @NotBlank(message = "패스워드는 필수 입니다.")
            String password
    ){};

    @Getter
    @Builder(toBuilder = true)  // toBuilder = true 추가
    public static class Response {
        private UserPayload user;
        private CouplePayload couple;
        private String message;
        private String sessionKey;
        private String nickname;
        private boolean success;
    }

    @Getter
    @Builder
    private static class UserPayload {
        private String id;
        private String username;
        private String nickname;
        private String email;
        private boolean emailVerified;
    }

    public static UserPayload toUserPayload(User user) {
        return UserPayload.builder()
                .id(user.getId())
                .username(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .emailVerified(user.getEmailVerified())
                .build();
    }

    @Getter
    @Builder
    public static class CouplePayload {
        private Long id;
        private String inviterId;
        private String inviteeId;
        private String coupleName;
        private LocalDate startDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    public static CouplePayload toCouplePayload(Couples couple) {
        return CouplePayload.builder()
                .id(couple.getId())
                .inviteeId(couple.getInviteeId())
                .inviterId(couple.getInviterId())
                .coupleName(couple.getCoupleName())
                .startDate(couple.getStartDate())
                .createdAt(couple.getCreatedAt())
                .updatedAt(couple.getUpdatedAt())
                .build();
    }
}

//{
//        "user": {
//        "id": "test",
//        "username": "test",
//        "nickname": "테스트유저",
//        "email": "",
//        "emailVerified": true
//        },
//        "couple": {
//        "id": 1,
//        "inviter_id": "test",
//        "invitee_id": "partner123",
//        "couple_name": "테스트커플",
//        "start_date": "2024-01-01",
//        "profile_image_url": null,
//        "description": "우리의 사랑 이야기",
//        "created_at": "2024-01-01T00:00:00Z",
//        "updated_at": "2024-01-01T00:00:00Z"
//        },
//        "accessToken": "2PD8SW3bneWfmMSlUxGWf4VpkGOqzQ58Y_6CBJbMScI",
//        "refreshToken": ""
//        }
