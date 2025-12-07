package dev.gunho.api.stock.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.gunho.api.global.util.Util;
import dev.gunho.api.stock.v1.entity.StockDailyEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TwelveDataTimeSeriesResponse(
        Meta meta,
        List<Value> values,
        String status,
        String message,
        String code
) {

    public List<Value> valuesOrEmpty() {
        return values == null ? Collections.emptyList() : values;
    }

    public boolean hasError() {
        return status != null && !"ok".equalsIgnoreCase(status);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Meta(
            @JsonProperty("symbol") String symbol,
            @JsonProperty("interval") String interval,
            @JsonProperty("currency") String currency,
            @JsonProperty("exchange") String exchange,
            @JsonProperty("mic_code") String micCode
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Value(
            @JsonProperty("datetime") String dateTime,
            @JsonProperty("open") String open,
            @JsonProperty("high") String high,
            @JsonProperty("low") String low,
            @JsonProperty("close") String close,
            @JsonProperty("volume") String volume
    ) {
        public LocalDate tradeDate() {
            return LocalDate.parse(dateTime.substring(0, 10));
        }
    }

    public StockDailyEntity toEntity(TwelveDataTimeSeriesResponse.Meta meta, TwelveDataTimeSeriesResponse.Value value) {
        LocalDate tradeDate = value.tradeDate();
        BigDecimal open = Util.CommonUtil.toBigDecimal(value.open());
        BigDecimal high = Util.CommonUtil.toBigDecimal(value.high());
        BigDecimal low = Util.CommonUtil.toBigDecimal(value.low());
        BigDecimal close = Util.CommonUtil.toBigDecimal(value.close());
        Long volume = Util.CommonUtil.toLong(value.volume());

        BigDecimal turnover = (close != null && volume != null)
                ? close.multiply(BigDecimal.valueOf(volume))
                : null;

        return StockDailyEntity.builder()
                .symbol(meta.symbol())
                .tradeDate(tradeDate)
                .openPrice(open)
                .highPrice(high)
                .lowPrice(low)
                .closePrice(close)
                .adjClose(close)
                .volume(volume)
                .turnover(turnover)
                .build();
    }
}
