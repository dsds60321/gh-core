package dev.gunho.api.bingous.v1.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class AnniversaryDto {

    public record Request(

            @NotBlank(message = "기념일 유형은 필수입니다.")
            String type,
            @NotBlank(message = "기념일 이름은 필수입니다.")
            @Size(max = 128, message = "128자를 초과 할 수 없습니다.")
            String title,
            @NotBlank(message = "날짜는 필수 입니다.")
            LocalDate date,
            boolean isContinue,
            String summary
    ){}

}
