package dev.gunho.api.ongimemo.v1.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    MALE("male"),
    FEMALE("female"),
    OTHER("other");

    private final String value;

    Gender(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Gender fromValue(String value) {
        if (value == null) {
            return OTHER;
        }

        for (Gender gender : Gender.values()) {
            if (gender.getValue().equalsIgnoreCase(value)) {
                return gender;
            }
        }

        // 기본값 반환 (예외 대신)
        return OTHER;
    }

    // 추가적인 안전한 변환 메서드
    public static Gender fromValueSafe(String value) {
        try {
            return fromValue(value);
        } catch (Exception e) {
            return OTHER;
        }
    }
}
