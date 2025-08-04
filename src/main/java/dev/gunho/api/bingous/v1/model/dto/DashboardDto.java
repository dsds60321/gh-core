package dev.gunho.api.bingous.v1.model.dto;

import dev.gunho.api.bingous.v1.model.entity.Anniversary;
import dev.gunho.api.bingous.v1.model.entity.BudgetItems;
import dev.gunho.api.bingous.v1.model.entity.Schedules;
import dev.gunho.api.bingous.v1.model.enums.AnniversariesType;
import dev.gunho.api.bingous.v1.model.enums.ScheduleType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class DashboardDto {

    @Getter
    @Builder
    public static class Response {
        private List<AnniversaryPayload> anniversaries;
        private List<SchedulePayload> schedules;
        private List<BudgetItemPayload> budget;
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
        private ScheduleType priority;
        private boolean completed;
        private String createdBy;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDate dueDate;
        private String dueTime;
    }

    @Getter
    @Builder
    public static class BudgetItemPayload {
        private Long id;
        private String paidBy;
        private String title;
        private String description;
        BigDecimal amount;
        String category;
        LocalDate date;
        String location;
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
        return SchedulePayload.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .priority(ScheduleType.valueOf(entity.getPriority().toUpperCase()))
                .completed(entity.isCompleted())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .dueDate(entity.getDueDate())
                .dueTime(String.valueOf(entity.getDueTime()))
                .build();
    }

    public static BudgetItemPayload toBudgetItems(BudgetItems entity) {
        return BudgetItemPayload.builder()
                .id(entity.getId())
                .paidBy(entity.getPaidBy())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .amount(entity.getAmount())
                .category(entity.getCategory())
                .date(entity.getExpenseDate())
                .location(entity.getLocation())
                .build();
    }
}
