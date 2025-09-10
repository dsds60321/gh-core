package dev.gunho.api.ongimemo.v1.model.entity;

import dev.gunho.api.ongimemo.v1.model.enums.FriendRequestStatus;
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
@Table(schema = "ongimemo", name = "friend_requests")
public class FriendRequest {

    @Id
    private Long idx;

    @Column("request_idx")
    private Long requestIdx;

    @Column("receiver_idx")
    private Long receiverIdx;

    private FriendRequestStatus status;

    private String message;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("responded_at")
    private LocalDateTime respondedAt;

}
