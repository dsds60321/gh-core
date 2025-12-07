package dev.gunho.api.ongimemo.v1.model.dto;

import dev.gunho.api.ongimemo.v1.model.enums.PraiseStatus;
import dev.gunho.api.ongimemo.v1.model.enums.ReflectionStatus;

import java.time.LocalDateTime;

public class PraiseDto {
    public record Request(String title, String content, PraiseStatus status, String[] recipients){}

    public record Response(String title, String content, PraiseStatus status, LocalDateTime createdAt, LocalDateTime updatedAt){}
    public record WithRecipientsResponse(String title, String content, PraiseStatus status, LocalDateTime createdAt, LocalDateTime updatedAt, String[] recipients){}
}
