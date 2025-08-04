package dev.gunho.api.bingous.v1.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ScheduleType {
    LOW,
    MEDIUM,
    HIGH;

    @JsonCreator
    public static ScheduleType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return MEDIUM; // 기본값
        }

        try {
            return ScheduleType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("Invalid priority value: %s. Valid values are: low, medium, high", value)
            );
        }
    }

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }
}
