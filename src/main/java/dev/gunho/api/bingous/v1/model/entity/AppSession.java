package dev.gunho.api.bingous.v1.model.entity;

import dev.gunho.api.bingous.v1.model.enums.DeviceType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("app_sessions")
public class AppSession {

    @Id
    private Long id; // Auto Increment - null이면 INSERT, 값이 있으면 UPDATE

    @Column("session_key")
    private String sessionKey;

    @Column("user_id")
    private String userId;

    @Column("device_id")
    private String deviceId;

    @Column("device_type")
    private DeviceType deviceType;

    @Column("device_name")
    private String deviceName;

    @Column("ip_address")
    private String ipAddress;

    @Column("user_agent")
    private String userAgent;

    @Column("is_active")
    private Boolean isActive;

    @Column("expires_at")
    private LocalDateTime expiresAt;

    @Column("last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @Column("created_at")
    private LocalDateTime createdAt;

    /**
     * 마지막 접근 시간 업데이트 (불변 객체로 새 인스턴스 반환)
     */
    public AppSession withUpdatedLastAccessed() {
        return AppSession.builder()
                .id(this.id)
                .sessionKey(this.sessionKey)
                .userId(this.userId)
                .deviceId(this.deviceId)
                .deviceType(this.deviceType)
                .deviceName(this.deviceName)
                .ipAddress(this.ipAddress)
                .userAgent(this.userAgent)
                .isActive(this.isActive)
                .expiresAt(this.expiresAt)
                .lastAccessedAt(LocalDateTime.now()) // 업데이트
                .createdAt(this.createdAt)
                .build();
    }

    /**
     * 세션 비활성화 (불변 객체로 새 인스턴스 반환)
     */
    public AppSession withDeactivated() {
        return AppSession.builder()
                .id(this.id)
                .sessionKey(this.sessionKey)
                .userId(this.userId)
                .deviceId(this.deviceId)
                .deviceType(this.deviceType)
                .deviceName(this.deviceName)
                .ipAddress(this.ipAddress)
                .userAgent(this.userAgent)
                .isActive(false) // 비활성화
                .expiresAt(this.expiresAt)
                .lastAccessedAt(LocalDateTime.now())
                .createdAt(this.createdAt)
                .build();
    }
}
