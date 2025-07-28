package dev.gunho.api.bingous.v1.model.dto;

import dev.gunho.api.bingous.v1.model.entity.Anniversary;
import dev.gunho.api.bingous.v1.model.enums.AnniversariesType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class AnniversaryDto {

    public record Request(

            @NotNull(message = "기념일 유형은 필수입니다.")
            AnniversariesType type,
            @NotBlank(message = "기념일 이름은 필수입니다.")
            @Size(max = 128, message = "128자를 초과 할 수 없습니다.")
            String title,
            @NotNull(message = "날짜는 필수 입니다.")
            LocalDate date,
            boolean isContinue,
            boolean isPrivate,
            String summary
    ){
        public Anniversary toEntity() {
            return Anniversary.builder()
                    .type(this.type)
                    .title(this.title)
                    .date(this.date)
                    .isContinue(this.isContinue)
                    .isPrivate(this.isPrivate)
                    .summary(this.summary)
                    .build();
        }

    }
}
