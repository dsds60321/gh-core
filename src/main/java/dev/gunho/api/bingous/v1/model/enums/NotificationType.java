package dev.gunho.api.bingous.v1.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum NotificationType {

    REFLECTION_CREATED("reflection_created"),
    REFLECTION_UPDATED("reflection_updated"),
    REFLECTION_APPROVED("reflection_approved"),
    REFLECTION_REJECTED("reflection_rejected");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static NotificationType fromValue(String value) {
        for (NotificationType type : NotificationType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown NotificationType: " + value);
    }

}
