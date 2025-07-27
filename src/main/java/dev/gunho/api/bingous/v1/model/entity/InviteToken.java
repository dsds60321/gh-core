package dev.gunho.api.bingous.v1.model.entity;

import dev.gunho.api.bingous.v1.model.enums.TokenStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("invite_tokens")
public class InviteToken {

    @Id
    private Long id;

    @Column
    private String token;

    @Column("inviter_id")
    private String inviterId;

    @Column("invitee_id")
    private String inviteeId;

    @Column("couple_name")
    private String coupleName;

    @Column
    private String message;

    @Column("status")
    private TokenStatus tokenStatus;

    @Column("expires_at")
    private LocalDateTime expiresAt;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;


    public static InviteToken toEntity(String token, String inviterId) {
        return InviteToken.builder()
                .token(token)
                .inviterId(inviterId)
                .tokenStatus(TokenStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();
    }

}
