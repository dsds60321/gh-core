package dev.gunho.api.global.enums;

import lombok.Getter;

@Getter
public enum ResponseCode {

     // 성공 응답
    SUCCESS("200", "성공", "요청이 성공적으로 처리되었습니다."),


     // 클라이언트 오류
    BAD_REQUEST("400", "잘못된 요청", "요청 파라미터를 확인해주세요."),

    // 서버 오류
    INTERNAL_SERVER_ERROR("500", "서버 오류", "서버에서 오류가 발생했습니다.");



    private final String code;
    private final String message;
    private final String description;

    ResponseCode(String code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }
}
