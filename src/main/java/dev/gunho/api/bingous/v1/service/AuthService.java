package dev.gunho.api.bingous.v1.service;

import dev.gunho.api.bingous.v1.model.dto.EmailVerify;
import dev.gunho.api.bingous.v1.model.dto.SignUp;
import dev.gunho.api.global.constants.CoreConstants;
import dev.gunho.api.global.enums.ResponseCode;
import dev.gunho.api.global.enums.TemplateCode;
import dev.gunho.api.global.model.dto.EmailResponse;
import dev.gunho.api.global.service.EmailService;
import dev.gunho.api.global.util.RedisUtil;
import dev.gunho.api.global.util.ServiceResult;
import dev.gunho.api.global.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final EmailService emailService;
    private final RedisUtil redisUtil;
    private final SessionService sessionService;

    /**
     * 회원가입
     */
    public Mono<ServiceResult<SignUp.Response>> signUp(SignUp.Request request, ServerHttpRequest httpRequest) {
        log.info("signUp called - ID: {}, Email: {}", request.id(), request.email());

        return userService.signUp(request, httpRequest)
                .flatMap(result -> {
                    if (result.isSuccess()) {
                        // 회원가입 성공 시 세션 생성
                        return sessionService.createSession(request.id(), httpRequest)
                                .map(session -> {
                                    // 기존 Response 데이터를 복사하여 새로운 Response 생성
                                    SignUp.Response originalResponse = result.getData();
                                    SignUp.Response responseWithSession = SignUp.Response.builder()
                                            .message(originalResponse.getMessage())
                                            .userId(originalResponse.getUserId())
                                            .success(originalResponse.isSuccess())
                                            .sessionKey(session.getSessionKey()) // 세션 키 추가
                                            .build();

                                    return ServiceResult.success(responseWithSession, "회원가입이 완료되었습니다.");
                                })
                                .onErrorResume(error -> {
                                    log.error("Session creation failed after signup", error);
                                    // 세션 생성 실패해도 회원가입은 성공했으므로 원본 결과 반환
                                    return Mono.just(ServiceResult.success(result.getData(), "회원가입은 완료되었지만 세션 생성에 실패했습니다."));
                                });
                    } else {
                        // Result를 ServiceResult로 변환
                        return Mono.just(ServiceResult.<SignUp.Response>failure(
                                ResponseCode.fromResultCode(result.getCode()),
                                result.getMessage()
                        ));
                    }
                })
                .onErrorResume(error -> {
                    log.error("Error in signUp", error);
                    return Mono.just(ServiceResult.<SignUp.Response>failure(ResponseCode.INTERNAL_SERVER_ERROR, error));
                });
    }

    /**
     * 이메일 인증 코드 전송
     */
    public Mono<ServiceResult<EmailResponse>> verifyEmail(EmailVerify.Request request) {
        String randomCode = Util.CommonUtil.generateRandomCode(6);
        String redisKey = CoreConstants.Key.EMAIL_VERIFY.formatted(request.email());

        log.info("verifyEmail - ID: {}, Email: {}, Code: {}", request.id(), request.email(), randomCode);

        return redisUtil.setString(redisKey, randomCode, Duration.ofMinutes(5))
                .flatMap(saved -> {
                    if (!saved) {
                        log.error("Redis save failed - Email: {}", request.email());
                        return Mono.just(ServiceResult.<EmailResponse>failure(ResponseCode.REDIS_ERROR));
                    }

                    return emailService.sendTemplateEmail(
                            TemplateCode.SIGN_UP_VERIFY,
                            List.of(request.id(), randomCode),
                            request.email()
                    ).map(emailResponse -> {
                        if (emailResponse.isSuccess()) {
                            return ServiceResult.success(emailResponse, "인증 코드가 전송되었습니다.");
                        } else {
                            return ServiceResult.<EmailResponse>failure(ResponseCode.EMAIL_SEND_FAILED, emailResponse.getMessage());
                        }
                    });
                })
                .onErrorResume(error -> {
                    log.error("Error in verifyEmail - Email: {}", request.email(), error);
                    return Mono.just(ServiceResult.<EmailResponse>failure(ResponseCode.EMAIL_SEND_FAILED, error));
                });
    }

    /**
     * 이메일 인증 코드 확인
     */
    public Mono<ServiceResult<EmailVerify.VerifyCodeResponse>> confirmEmail(EmailVerify.VerifyCodeRequest request) {
        String redisKey = CoreConstants.Key.EMAIL_VERIFY.formatted(request.email());

        log.info("confirmEmail - Email: {}, Code: {}", request.email(), request.code());

        return redisUtil.getString(redisKey)
                .timeout(Duration.ofSeconds(10))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Redis key not found or expired: {}", redisKey);
                    return Mono.just((String) null);
                }))
                .flatMap(storedCode -> {
                    if (storedCode == null) {
                        return Mono.just(ServiceResult.<EmailVerify.VerifyCodeResponse>failure(ResponseCode.CODE_EXPIRED));
                    }

                    if (!storedCode.equalsIgnoreCase(request.code())) {
                        return Mono.just(ServiceResult.<EmailVerify.VerifyCodeResponse>failure(ResponseCode.INVALID_CODE));
                    }

                    // 인증 성공 시 Redis에서 코드 삭제 (delete 메서드 사용)
                    return redisUtil.delete(redisKey)
                            .thenReturn(ServiceResult.success(
                                    EmailVerify.VerifyCodeResponse.builder()
                                            .email(request.email())
                                            .verified(true)
                                            .message("인증에 성공했습니다.")
                                            .build(),
                                    "인증에 성공했습니다."
                            ));
                })
                .onErrorResume(error -> {
                    log.error("Error in confirmEmail - Email: {}", request.email(), error);
                    return Mono.just(ServiceResult.<EmailVerify.VerifyCodeResponse>failure(ResponseCode.INTERNAL_SERVER_ERROR, error));
                });
    }
}
