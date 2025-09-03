package dev.gunho.api.ongimemo.v1.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class SignUpCompleteDTO {

    public record Request(
            @NotBlank(message = "이메일은 필수입니다")
            @Email(message = "올바른 이메일 형식이 아닙니다")
            String email,

            @NotBlank(message = "인증코드는 필수입니다")
            @Size(min = 6, max = 6, message = "인증코드는 6자리입니다")
            String verificationCode
    ) {}

    @Builder
    public record Response(
            boolean success,
            String message,
            String userId,
            String nickname,
            String sessionKey  // 자동 로그인용
    ) {}

}
