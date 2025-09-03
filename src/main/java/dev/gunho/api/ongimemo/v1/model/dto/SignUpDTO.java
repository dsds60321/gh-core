package dev.gunho.api.ongimemo.v1.model.dto;

import dev.gunho.api.ongimemo.v1.model.enums.Gender;
import dev.gunho.api.ongimemo.v1.model.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class SignUpDTO {

    public record Request(
            @NotBlank(message = "아이디는 필수입니다")
            @Size(min = 1, max = 36, message = "ID는 1-36 사이여야 합니다")
            String id,
            @NotBlank(message = "패스워드는 필수입니다.")
            String password,
            @NotBlank(message = "이메일은 필수입니다")
            @Email(message = "올바른 이메일 형식이 아닙니다")
            String email,

            @NotBlank(message = "별칭은 필수입니다")
            String nickname,
            String phoneNumber,
            Gender gender,
            UserStatus status,
            Boolean email_verified,
            String token
    ){}

    @Builder
    public record Response(String id, String email, String nickname) {}

}
