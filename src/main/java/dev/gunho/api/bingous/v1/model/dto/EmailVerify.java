package dev.gunho.api.bingous.v1.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * 이메일 인증 DTO
 */
public class EmailVerify {

    public record Request(
            @NotBlank(message = "ID는 필수입니다")
            @Size(min = 1, max = 36, message = "ID는 1-36자 사이여야 합니다")
            String id,

            @NotBlank(message = "이메일은 필수입니다")
            @Email(message = "올바른 이메일 형식이 아닙니다")
            String email
    ) {}

    @Builder
    public record Response(
            String id,
            String email,
            String code,
            String expiresAt
    ) {}

    public record VerifyCodeRequest(
            @NotBlank(message = "이메일은 필수입니다")
            @Email(message = "올바른 이메일 형식이 아닙니다")
            String email,

            @NotBlank(message = "인증 코드는 필수입니다")
            @Pattern(regexp = "^\\d{6}$", message = "인증 코드는 6자리 숫자여야 합니다")
            String code
    ) {}

    @Builder(toBuilder = true)
    public record VerifyCodeResponse(
            boolean verified,
            String message
    ) {}


}
