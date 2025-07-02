package dev.gunho.api.bingous.v1.service;

import dev.gunho.api.bingous.v1.model.dto.EmailVerify;
import dev.gunho.api.bingous.v1.model.dto.SignUp;
import dev.gunho.api.global.enums.TemplateCode;
import dev.gunho.api.global.model.dto.EmailResponse;
import dev.gunho.api.global.service.EmailService;
import dev.gunho.api.global.util.RedisUtil;
import dev.gunho.api.global.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmailService emailService;
    private final RedisUtil redisUtil;

    public Mono<EmailResponse> verifyEmail(EmailVerify.Request request) {
        String randomCode = Util.CommonUtil.generateRandomCode(6);
        String redisKey = "email_verify:" + request.email();

        log.info("verifyEmail - ID: {}, Email: {}, Random code: {}",request.id(), request.email(), randomCode);

        return redisUtil.setString(redisKey, randomCode, Duration.ofMinutes(5))
                .flatMap(saved -> {
                    if (!saved) {
                        log.error("Error in verifyEmail - Email: {}", request.email());
                        return Mono.just(EmailResponse.builder()
                                .success(false)
                                .message("인증 코드 저장에 실패했습니다. 다시 시도해주세요.")
                                .errorCode("REDIS_SAVE_FAILED")
                                .build());
                    }

                    return emailService.sendTemplateEmail(
                            TemplateCode.SIGN_UP_VERIFY,
                            List.of(request.id(), randomCode),
                            request.email()
                    );
                })
                .onErrorResume(error -> {
                    log.error("Error in verifyEmail - Email: {}, Error: {}", request.email(), error.getMessage(), error);

                    return Mono.just(EmailResponse.builder()
                            .success(false)
                            .message("이메일 인증 요청 처리 중 오류가 발생했습니다.")
                            .errorCode("EMAIL_VERIFY_FAILED")
                            .build());
                });

    }
}
