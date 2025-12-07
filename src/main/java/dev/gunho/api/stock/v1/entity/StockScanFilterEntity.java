package dev.gunho.api.stock.v1.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static dev.gunho.api.stock.v1.enums.StockEnums.*;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "stock", name = "stock_scan_filter")
public class StockScanFilterEntity {

    @Id
    @Column("filter_id")
    private Long filterId;

    @Column("profile_id")
    private Long profileId;

    @Column("filter_type")
    private FilterType filterType;

    @Column("period_days")
    private Integer periodDays;

    @Column("compare_target")
    private CompareTarget compareTarget;

    @Column("operator")
    private Operator operator;

    @Column("threshold_value")
    private BigDecimal thresholdValue;

    @Column("threshold_value_to")
    private BigDecimal thresholdValueTo;

    @Column("min_value")
    private BigDecimal minValue;

    @Column("max_value")
    private BigDecimal maxValue;

    @Column("unit")
    private Unit unit;

    @Column("sort_order")
    private Integer sortOrder;

    @Column("active_yn")
    private String activeYn;

    @Column("extra_json")
    private String extraJson; // JSON 그대로 문자열로 관리

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

}
