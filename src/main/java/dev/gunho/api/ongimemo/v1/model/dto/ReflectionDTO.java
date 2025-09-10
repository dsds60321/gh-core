package dev.gunho.api.ongimemo.v1.model.dto;

import dev.gunho.api.ongimemo.v1.model.enums.ReflectionStatus;

import java.time.LocalDateTime;

public class ReflectionDTO {

    public record Request(String title, String content, String reward, ReflectionStatus status, String[] recipients){}

    public record Response(String title, String content, String reward, ReflectionStatus status, LocalDateTime createdAt, LocalDateTime updatedAt){}

    public record WithRecipientsResponse(String title, String content, String reward, ReflectionStatus status, LocalDateTime createdAt, LocalDateTime updatedAt, String[] recipients){}
}
