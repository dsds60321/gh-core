package dev.gunho.api.ongimemo.v1.model.entity;

import dev.gunho.api.ongimemo.v1.model.enums.ReflectionStatus;
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
@Table(schema = "ongimemo", name = "reflections")
public class Reflection {

    @Id
    private Long idx;

    @Column("user_idx")
    private Long userIdx;

    private String title;

    private String content;

    private String reward;

    private ReflectionStatus status;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

}
