package dev.gunho.api.global.model.dto;

import dev.gunho.api.bingous.v1.model.entity.AppSession;

import java.time.LocalDateTime;

public record SessionDto(String sessionKey, String userId, String ipAddress, boolean isActive, LocalDateTime expiredAt, LocalDateTime lastAccessedAt, LocalDateTime createdAt) {
    public static SessionDto from(AppSession session) {
        return new SessionDto(
                session.getSessionKey(),
                session.getUserId(),
                session.getIpAddress(),
                session.getIsActive(),
                session.getExpiresAt(),
                session.getLastAccessedAt(),
                session.getCreatedAt()
        );
    }
}
