package dev.gunho.api.bingous.v1.model.entity;

import dev.gunho.api.bingous.v1.model.enums.AnniversariesType;
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
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("anniversaries")
public class Anniversary {

    @Id
    private Long id;

    @Column("couple_id")
    private Long coupleId;

    @Column
    private AnniversariesType type;

    @Column
    private String title;

    @Column
    private LocalDate date;

    @Column("is_continue")
    private boolean isContinue;

    @Column("is_private")
    private boolean isPrivate;

    @Column
    private String summary;

    @Column("created_by")
    private String createdBy;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

}
