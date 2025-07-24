package dev.gunho.api.bingous.v1.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DeviceType {
    IOS("ios"),
    ANDROID("android"),
    WEB("web"),
    UNKNOWN("unknown");

    private final String value;

    DeviceType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DeviceType fromValue(String value) {
        if (value == null) {
            return UNKNOWN;
        }

        for (DeviceType deviceType : DeviceType.values()) {
            if (deviceType.getValue().equalsIgnoreCase(value)) {
                return deviceType;
            }
        }

        // 기본값 반환 (예외 대신)
        return UNKNOWN;
    }

    // 추가적인 안전한 변환 메서드
    public static DeviceType fromValueSafe(String value) {
        try {
            return fromValue(value);
        } catch (Exception e) {
            return UNKNOWN;
        }
    }
}
