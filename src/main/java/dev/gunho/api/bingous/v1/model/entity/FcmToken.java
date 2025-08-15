package dev.gunho.api.bingous.v1.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("fcm_tokens")
public class FcmToken {
    @Id
    private Long id;

    @Column("user_id")
    private String userId;

    @Column("fcm_token")
    private String fcmToken;

    @Column("device_id")
    private String deviceId;

    @Column("platform")
    private String platform; // iOS, Android

    @Column("is_active")
    private Boolean isActive;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
