package dev.gunho.api.stock.v1.enums;

import lombok.Getter;

public class TwelveDataEnums {

    @Getter
    public enum PATH {
        TIME_SERIES_DAILY("/time_series");

        String value;

        PATH(String value) {
            this.value = value;
        }
    }

    @Getter
    public enum INTERVAL {
        ONE_MIN("1min"),
        FIVE_MIN("5min"),
        FIFTEEN_MIN("15min"),
        THIRTY_MIN("30min"),
        FORTY_FIVE_MIN("45min"),
        ONE_HOUR("1h"),
        TWO_HOUR("2h"),
        FOUR_HOUR("4h"),
        EIGHT_HOUR("5h"),
        ONE_DAY("1day"),
        ONE_WEEK("1week"),
        ONE_MONTH("1month");

        String value;

        INTERVAL(String value) {
            this.value = value;
        }
    }
}
