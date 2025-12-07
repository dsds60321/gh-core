package dev.gunho.api.stock.v1.model;

import dev.gunho.api.stock.v1.enums.TwelveDataEnums.INTERVAL;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record TwelveDataTimeSeriesRequest(
        String symbol,
        INTERVAL interval,
        LocalDate startDate,
        LocalDate endDate
) {

    public TwelveDataTimeSeriesRequest of(String symbol, LocalDate startDate, LocalDate endDate){
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

        INTERVAL interval = INTERVAL.ONE_MONTH;

        if (1 > daysBetween) {
            interval = INTERVAL.ONE_DAY;
        } else if ( daysBetween > 1 &&  7 >= daysBetween) {
            interval = INTERVAL.ONE_WEEK;
        } else if ( daysBetween > 7 && 30 >= daysBetween) {
            interval = INTERVAL.ONE_MONTH;
        }

        return new TwelveDataTimeSeriesRequest(symbol, interval, startDate, endDate);
    }
}
