package dev.gunho.api.bingous.v1.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

public class SignIn {

    public record Request(
            @NotBlank(message = "ID는 필수입니다")
            String id,
            @NotBlank(message = "패스워드는 필수 입니다.")
            String password
    ){};

    @Getter
    @Builder(toBuilder = true)  // toBuilder = true 추가
    public static class Response {
        private String message;
        private String userId;
        private String sessionKey;
        private String nickname;
        private boolean success;
    }
}
