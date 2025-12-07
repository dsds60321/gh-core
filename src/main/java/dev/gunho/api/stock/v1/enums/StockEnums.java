package dev.gunho.api.stock.v1.enums;

public class StockEnums {

    public enum StockUniverseType {
        ALL,SYMBOL_LIST
    }

    public enum FilterMode {
        MATCH_ALL,
        MATCH_ANY
    }

    public enum FilterType {
        VOLUME,          // 거래량
        TURNOVER,        // 거래대금
        CHANGE_PERCENT,  // 금일 등락률 %
        CHANGE_FROM_HIGH,// 고점 대비 하락률
        CUSTOM           // 커스텀
    }

    public enum CompareTarget {
        TODAY_VS_YESTERDAY,
        TODAY_VS_NDAY_AVG,
        TODAY_ONLY
    }

    public enum Operator {
        GT, GTE, LT, LTE, EQ, BETWEEN
    }

    public enum Unit {
        PERCENT,
        CURRENCY,
        SHARES
    }

    public enum RunStatus {
        SUCCESS,
        FAIL,
        PARTIAL
    }

}
