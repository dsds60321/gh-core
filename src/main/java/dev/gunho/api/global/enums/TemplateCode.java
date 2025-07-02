package dev.gunho.api.global.enums;

import lombok.Getter;

@Getter
public enum TemplateCode {

    SIGN_UP_VERIFY("EMAIL", "SIGNUP_VERIFY");

    private final String type;
    private final String name;

    TemplateCode(String type, String name) {
        this.type = type;
        this.name = name;
    }
}
