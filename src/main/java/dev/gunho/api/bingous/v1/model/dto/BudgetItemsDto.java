package dev.gunho.api.bingous.v1.model.dto;

import dev.gunho.api.bingous.v1.model.entity.BudgetItems;
import dev.gunho.api.bingous.v1.model.enums.BudgetCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BudgetItemsDto {

    public record Request(
            @NotBlank(message = "결제 대상은 필수값 입니다.")
            String paidBy,
            @NotBlank(message = "내용은 필수 값 입니다.")
            String title,
            String description,
            @NotNull(message = "금액은 필수 값 입니다.")
            BigDecimal amount,
            @NotNull(message = "카테고리는 필수 값 입니다.")
            String category,
            @NotNull(message = "날짜는 필수 값 입니다.")
            LocalDate date,
            String location
    ){
        public BudgetItems toEntity() {
            return BudgetItems.builder()
                    .paidBy(this.paidBy)
                    .title(this.title)
                    .description(this.description)
                    .amount(this.amount)
                    .category(this.category)
                    .expenseDate(this.date)
                    .location(this.location)
                    .build();
        }

    }
}
