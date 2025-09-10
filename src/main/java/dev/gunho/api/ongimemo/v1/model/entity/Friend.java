package dev.gunho.api.ongimemo.v1.model.entity;

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
@Table(schema = "ongimemo", name = "friends")
public class Friend {

    @Id
    private Long idx;

    @Column("user_idx")
    private Long userIdx;

    @Column("friend_idx")
    private Long friendIdx;

    private boolean status;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

}
