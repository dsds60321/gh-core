package dev.gunho.api.stock.v1.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "stock", name = "stock_daily")
public class StockDailyEntity {

    @Id
    @Column("id")
    private Long id;

    @Column("symbol")
    private String symbol;

    @Column("trade_date")
    private LocalDate tradeDate;

    @Column("open_price")
    private BigDecimal openPrice;

    @Column("high_price")
    private BigDecimal highPrice;

    @Column("low_price")
    private BigDecimal lowPrice;

    @Column("close_price")
    private BigDecimal closePrice;

    @Column("volume")
    private Long volume;

    @Column("turnover")
    private BigDecimal turnover;

    @Column("adj_close")
    private BigDecimal adjClose;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

}
