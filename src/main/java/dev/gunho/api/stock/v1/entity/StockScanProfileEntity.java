package dev.gunho.api.stock.v1.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static dev.gunho.api.stock.v1.enums.StockEnums.FilterMode;
import static dev.gunho.api.stock.v1.enums.StockEnums.StockUniverseType;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "stock", name = "stock_scan_profile")
public class StockScanProfileEntity {

    @Id
    @Column("profile_id")
    private Long profileId;

    @Column("profile_name")
    private String profileName;

    @Column("description")
    private String description;

    @Column("start_day")
    private LocalDate startDay;

    @Column("end_day")
    private LocalDate endDay;

    @Column("stock_universe_type")
    private StockUniverseType stockUniverseType;

    @Column("stock_symbols")
    private String stockSymbols;    // "AAPL,MSFT,QQQ"

    @Column("lookback_days")
    private Integer lookbackDays;

    @Column("filter_mode")
    private FilterMode filterMode;

    @Column("to_email")
    private String toEmail;

    @Column("cc_email")
    private String ccEmail;

    @Column("cron_expression")
    private String cronExpression;

    @Column("active_yn")
    private String activeYn;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
