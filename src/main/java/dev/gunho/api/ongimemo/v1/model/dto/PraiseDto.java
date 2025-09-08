package dev.gunho.api.ongimemo.v1.model.dto;

import dev.gunho.api.ongimemo.v1.model.enums.PraiseStatus;

import java.time.LocalDateTime;

public class PraiseDto {
    public record Request(String title, String content){}

    public record Response(String title, String content, PraiseStatus status, LocalDateTime createdAt, LocalDateTime updatedAt){}
    public record WithRecipientsResponse(String title, String content, PraiseStatus status, LocalDateTime createdAt, LocalDateTime updatedAt, String[] recipients){}
}
