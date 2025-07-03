package dev.gunho.api.bingous.v1.service;

import dev.gunho.api.bingous.v1.model.dto.EmailVerify;
import dev.gunho.api.bingous.v1.model.dto.SignUp;
import dev.gunho.api.bingous.v1.repository.UserRepository;
import dev.gunho.api.global.constants.CoreConstants;
import dev.gunho.api.global.enums.TemplateCode;
import dev.gunho.api.global.model.Result;
import dev.gunho.api.global.model.dto.EmailResponse;
import dev.gunho.api.global.service.EmailService;
import dev.gunho.api.global.util.RedisUtil;
import dev.gunho.api.global.util.ResponseUtil;
import dev.gunho.api.global.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService; // UserService 의존성 추가
    private final EmailService emailService;
    private final RedisUtil redisUtil;

    /**
     * 회원가입 - UserService에 위임
     */
    public Mono<Result<SignUp.Response>> signUp(SignUp.Request request, ServerHttpRequest httpRequest) {
        log.info("signUp called - ID: {}, Email: {}", request.id(), request.email());
        return userService.signUp(request, httpRequest);
    }



    /**
     * 이메일 코드 요청
     */
    public Mono<EmailResponse> verifyEmail(EmailVerify.Request request) {
        String randomCode = Util.CommonUtil.generateRandomCode(6);
        String redisKey = CoreConstants.Key.EMAIL_VERIFY.formatted(request.email());

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

    /**
     * 이메일 검증 확인
     */
    public Mono<EmailVerify.VerifyCodeResponse> confirmEmail(EmailVerify.VerifyCodeRequest verifyCodeRequest) {

        String redisKey = CoreConstants.Key.EMAIL_VERIFY.formatted(verifyCodeRequest.email());

        log.info("verifyEmail - ID: {}, Email: {}" ,verifyCodeRequest.email(), verifyCodeRequest.code());

        return redisUtil.getString(redisKey)
                .flatMap(code -> {
                    if (code.equalsIgnoreCase(verifyCodeRequest.code())) {
                        return Mono.just(EmailVerify.VerifyCodeResponse.builder()
                                .verified(true)
                                .message("인증에 성공했습니다.")
                                .build());
                    }

                    return Mono.just(EmailVerify.VerifyCodeResponse.builder()
                            .verified(false)
                            .message("인증에 실패했습니다.")
                            .build());
                })
                .onErrorResume(error -> {

                    log.error("Error in confirmEmail - Email: {}, Error: {}", verifyCodeRequest.email(), error.getMessage(), error);

                    return Mono.just(EmailVerify.VerifyCodeResponse.builder()
                            .verified(false)
                            .message("이메일 인증 요청 처리 중 오류가 발생했습니다.")
                            .build());
                });
    }
}
