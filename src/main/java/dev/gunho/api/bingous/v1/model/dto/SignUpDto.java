package dev.gunho.api.bingous.v1.model.dto;

import dev.gunho.api.bingous.v1.model.entity.User;
import dev.gunho.api.bingous.v1.model.enums.Gender;
import dev.gunho.api.bingous.v1.model.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

public class SignUpDto {

    public record Request(
            @NotBlank(message = "ID는 필수입니다")
            @Size(min = 1, max = 36, message = "ID는 1-36 사이여야 합니다")
            String id,

            @NotBlank(message = "이메일은 필수입니다")
            @Email(message = "올바른 이메일 형식이 아닙니다")
            String email,

            @NotBlank(message = "별칭은 필수입니다")
            String nickname,

            @NotBlank(message = "패스워드는 필수 입니다.")
            String password,

            String phoneNumber,
            Gender gender,
            UserStatus status,
            Boolean email_verified
    ) {
        public User toEntity(PasswordEncoder passwordEncoder) {
            return User.builder()
                    .id(this.id)
                    .email(this.email)
                    .nickname(this.nickname)
                    .passwordHash(passwordEncoder.encode(this.password))
                    .phone(this.phoneNumber)
                    .gender(this.gender != null ? this.gender : Gender.OTHER)
                    .status(this.status != null ? this.status : UserStatus.ACTIVE)
                    .emailVerified(this.email_verified != null ? this.email_verified : false)
                    .phoneVerified(false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }
    }

    @Getter
    @Builder(toBuilder = true)  // toBuilder = true 추가
    public static class Response {
        private String message;
        private String userId;
        private String sessionKey;
        private boolean success;
    }
}
