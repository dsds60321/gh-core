package dev.gunho.api.bingous.v1.model.enums;

public enum Gender {
    MALE("male"),
    FEMALE("female"),
    OTHER("other");

    private final String value;

    Gender(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Gender fromValue(String value) {
        for (Gender gender : Gender.values()) {
            if (gender.getValue().equals(value)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Unknown gender: " + value);
    }
}
