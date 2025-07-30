package dev.gunho.api.bingous.v1.repository;

import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import dev.gunho.api.bingous.v1.model.entity.BudgetItems;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface BudgetItemsRepository extends ReactiveCrudRepository<BudgetItems, Long> {

    @Query("""
        INSERT INTO budget_items 
        (couple_id, paid_by, title, description, location, amount, category, expense_date, created_by, created_at, updated_at) 
        VALUES 
        (:coupleId, :paidBy, :title, :description, :location, :amount, :category, :expenseDate, :createdBy, :createdAt, :updatedAt)
    """)
    Mono<BudgetItems> insertBudgetItem(
            Long coupleId,
            String paidBy,
            String title,
            String description,
            String location,
            BigDecimal amount,
            String category, // Enum은 String으로 변환해 전달
            LocalDate expenseDate,
            String createdBy,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    );

    @Query("SELECT * FROM budget_items WHERE couple_id = :coupleId AND TO_CHAR(expense_date, 'YYYYMM') = :month")
    Flux<BudgetItems> findAllByMonth(String month, Long coupleId);

    @Query("SELECT * FROM budget_items WHERE id = LAST_INSERT_ID()")
    Mono<BudgetItems> findLastInsertedBudgetItem();

}

