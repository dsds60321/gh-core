package dev.gunho.api.bingous.v1.model.entity;

import dev.gunho.api.bingous.v1.model.dto.ReflectionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("reflections")
public class Reflection {

    @Id
    private Long id;

    @Column("couple_id")
    private Long coupleId;

    @Column("author_user_id")
    private String authorUserId;

    @Column("approver_user_id")
    private String approverUserId;

    @Column("incident")
    private String incident; // 잘못한 일

    @Column("reason")
    private String reason; // 잘못한 이유

    @Column("improvement")
    private String improvement; // 개선 방안

    @Column("status")
    private String status; // enum: 'PENDING', 'APPROVED', 'REJECTED'

    @Column("feedback")
    private String feedback; // 반려 시 피드백

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("approved_at")
    private LocalDateTime approvedAt; // 결재 처리 시간

    // 편의 메서드들
    public boolean isPending() {
        return "PENDING".equals(status);
    }

    public boolean isApproved() {
        return "APPROVED".equals(status);
    }

    public boolean isRejected() {
        return "REJECTED".equals(status);
    }

    public Reflection withStatus(String status) {
        return this.toBuilder()
                .status(status)
                .approvedAt(LocalDateTime.now())
                .build();
    }

    public Reflection withApprover(String approverUserId) {
        return this.toBuilder()
                .approverUserId(approverUserId)
                .build();
    }

    public Reflection withFeedback(String feedback) {
        return this.toBuilder()
                .feedback(feedback)
                .build();
    }

    // 생성용 정적 메서드
    public static Reflection createNew(ReflectionDto.Request request) {
        return Reflection.builder()
                .coupleId(request.coupleId())
                .authorUserId(request.authorUserId())
                .approverUserId(request.approverUserId())
                .incident(request.incident())
                .reason(request.reason())
                .improvement(request.improvement())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 승인 처리
    public Reflection approve(String approverUserId) {
        return this.toBuilder()
                .status("APPROVED")
                .approverUserId(approverUserId)
                .approvedAt(LocalDateTime.now())
                .build();
    }

    // 반려 처리
    public Reflection reject(String approverUserId, String feedback) {
        return this.toBuilder()
                .status("REJECTED")
                .approverUserId(approverUserId)
                .feedback(feedback)
                .approvedAt(LocalDateTime.now())
                .build();
    }
}
