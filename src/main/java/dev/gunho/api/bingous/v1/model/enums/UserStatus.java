package dev.gunho.api.bingous.v1.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    SUSPENDED("suspended"),
    DELETED("deleted");

    private final String value;

    UserStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static UserStatus fromValue(String value) {
        if (value == null) {
            return ACTIVE;
        }

        for (UserStatus status : UserStatus.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }

        return ACTIVE; // 기본값
    }
}
