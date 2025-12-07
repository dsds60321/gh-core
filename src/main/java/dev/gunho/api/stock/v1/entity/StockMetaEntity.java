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

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "stock", name = "stock_meta")
public class StockMetaEntity {

    @Id
    @Column("symbol")
    private String symbol;

    @Column("name")
    private String name;

    @Column("is_etf")
    @Builder.Default
    private boolean isEtf = false;

    @Column("exchange")
    private String exchange;

    @Column("currency")
    @Builder.Default
    private String currency = "USD";

    @Column("sector")
    private String sector;

    @Column("industry")
    private String industry;

    @Column("country")
    private String country;

    @Column("ipo_date")
    private LocalDate ipoDate;

    @Column("active_yn")
    @Builder.Default
    private String activeYn = "Y";

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
