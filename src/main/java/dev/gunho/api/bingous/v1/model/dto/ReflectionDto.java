package dev.gunho.api.bingous.v1.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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
}
