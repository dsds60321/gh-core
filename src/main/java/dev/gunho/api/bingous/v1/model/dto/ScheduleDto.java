package dev.gunho.api.bingous.v1.model.dto;

import dev.gunho.api.bingous.v1.model.enums.ScheduleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public class ScheduleDto {

    public record Request(

            @NotNull(message = "우선순위는 필수입니다.")
            ScheduleType priority,
            @NotBlank(message = "할일 제목은 필수입니다.")
            @Size(max = 200, message = "200자를 초과할 수 없습니다.")
            String title,
            String description,
            @NotNull(message = "마감 날짜는 필수입니다.")
            LocalDate dueDate,
            LocalTime dueTime
    ){}
}
