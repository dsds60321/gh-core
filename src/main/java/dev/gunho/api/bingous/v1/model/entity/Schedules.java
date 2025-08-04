package dev.gunho.api.bingous.v1.model.entity;

import dev.gunho.api.bingous.v1.model.dto.ScheduleDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("schedules")
public class Schedules {

    @Id
    private Long id;

    @Column("couple_id")
    private Long coupleId;

    @Column("title")
    private String title;

    @Column("description")
    private String description;

    @Column("priority")
    private String priority; // enum: 'low', 'medium', 'high'

    @Column("completed")
    private Boolean completed;

    @Column("created_by")
    private String createdBy;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("due_date")
    private LocalDate dueDate;

    @Column("due_time")
    private LocalTime dueTime;

    // 편의 메서드들
    public boolean isCompleted() {
        return completed != null && completed;
    }

    public Schedules withCompleted(boolean completed) {
        return this.toBuilder()
                .completed(completed)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Schedules withUpdatedAt() {
        return this.toBuilder()
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 생성용 정적 메서드
    public static Schedules createNew(ScheduleDto.Request request, Long coupleId, String createdBy) {
        return Schedules.builder()
                .coupleId(coupleId)
                .title(request.title())
                .description(request.description())
                .priority(request.priority().toString())
                .completed(false)
                .dueDate(request.dueDate())
                .dueTime(request.dueTime())
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
