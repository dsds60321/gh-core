package dev.gunho.api.bingous.v1.model.entity;

import dev.gunho.api.bingous.v1.model.enums.BudgetCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("budget_items")
public class BudgetItems {

    @Id
    private Long id;

    @Column("couple_id")
    private Long coupleId;

    @Column("paid_by")
    private String paidBy;

    @Column("title")
    private String title;

    @Column("description")
    private String description;

    @Column("location")
    private String location;

    @Column("amount")
    private BigDecimal amount;

    @Column("category")
    private String category;

    @Column("expense_date")
    private LocalDate expenseDate;

    @Column("created_by")
    private String createdBy;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
