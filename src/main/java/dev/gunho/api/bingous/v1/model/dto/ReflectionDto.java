package dev.gunho.api.bingous.v1.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ReflectionDto {

    public record Request(
            @JsonProperty("couple_id")
            Long coupleId,
            @JsonProperty("author_user_id")
            String authorUserId,
            @JsonProperty("approver_user_id")
            String approverUserId,
            String incident,
            String reason,
            String improvement
    ){}

    public record StatusUpdate(
            @NotBlank(message = "상태는 필수입니다.")
            @Pattern(regexp = "^(APPROVED|REJECTED)$", message = "상태는 APPROVED 또는 REJECTED만 가능합니다.")
            String status,

            String feedback  // REJECTED일 때 사용
    ){}

}
