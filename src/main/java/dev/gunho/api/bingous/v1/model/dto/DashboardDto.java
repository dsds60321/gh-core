package dev.gunho.api.bingous.v1.model.dto;

import dev.gunho.api.bingous.v1.model.entity.Anniversary;
import dev.gunho.api.bingous.v1.model.entity.Schedules;
import dev.gunho.api.bingous.v1.model.enums.AnniversariesType;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

public class DashboardDto {

    @Getter
    @Builder
    public static class Response {
        private List<AnniversaryPayload> anniversaries;
        private List<SchedulePayload> schedules;
        private int completedTasksThisWeek;
        private int pendingTasksCount;
        private Stats stats;
    }

    @Getter
    @Builder
    public static class AnniversaryPayload {
        private Long id;
        private AnniversariesType type;
        private String title;
        private String date;
        private boolean isContinue;
        private boolean isPrivate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    public static class SchedulePayload {
        private Long id;
        private String title;
        private String description;
        private String date;
        private String location;
        private String priority;
        private String status;
        private String assignedTo;
        private String category;
        private int estimatedDuration;
        private List<String> tags;
        private LocalDateTime completedAt;
        private String completedBy;
        private String createdBy;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    public static class Stats {
        private int totalAnniversaries;
        private int completedTasksThisMonth;
        private int pendingTasks;
        private int thisMonthTasks;
        private int daysFromStart;
    }

    public static AnniversaryPayload toAnniversary(Anniversary entity) {
        return DashboardDto.AnniversaryPayload.builder()
                .id(entity.getId())
                .type(entity.getType())
                .title(entity.getTitle())
                .date(entity.getDate().toString())
                .isContinue(entity.isContinue())
                .isPrivate(entity.isPrivate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

    }

    public static SchedulePayload toSchedule(Schedules entity) {
        return DashboardDto.SchedulePayload.builder()
                .id(entity.getId())
                .build();
    }

}
