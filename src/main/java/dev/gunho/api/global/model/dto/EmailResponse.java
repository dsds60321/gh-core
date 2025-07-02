package dev.gunho.api.global.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailResponse {

    @Builder.Default
    private boolean success = true;
    @Builder.Default
    private String message = "이메일이 성공적으로 발송되었습니다.";
    private String errorCode;

}
