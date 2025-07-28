package dev.gunho.api.bingous.v1.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("couples")
public class Couples {

    @Id
    private Long id;

    @Column("inviter_id")
    private String inviterId;

    @Column("invitee_id")
    private String inviteeId;

    @Column("couple_name")
    private String coupleName;

    @Column("start_date")
    private LocalDate startDate;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    public static Couples toEntity(String inviter_id, String invitee_id) {
        return Couples.builder()
                .inviterId(inviter_id)
                .inviteeId(invitee_id)
                .coupleName(inviter_id + "_" + invitee_id)
                .build();
    }
}
