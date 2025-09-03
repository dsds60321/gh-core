package dev.gunho.api.ongimemo.v1.service;

import dev.gunho.api.bingous.v1.service.SessionService;
import dev.gunho.api.global.constants.CoreConstants.Key;
import dev.gunho.api.global.enums.TemplateCode;
import dev.gunho.api.global.exception.CustomException;
import dev.gunho.api.global.service.EmailService;
import dev.gunho.api.global.util.RedisUtil;
import dev.gunho.api.global.util.Util;
import dev.gunho.api.ongimemo.v1.model.dto.SignUpCompleteDTO;
import dev.gunho.api.ongimemo.v1.model.dto.SignUpDTO;
import dev.gunho.api.ongimemo.v1.model.entity.User;
import dev.gunho.api.ongimemo.v1.repository.OngiMemoUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static dev.gunho.api.global.constants.CoreConstants.Key.ONGI_EMAIL_VERIFY;
import static dev.gunho.api.global.util.Util.CommonUtil.generateRandomCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final SessionService sessionService;
    private final OngiMemoUserRepository ongiMemoUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;
    private final EmailService emailService;

    /**
     * 회원가입
     * 요청
     */
    public Mono<SignUpDTO.Response> requestSignUp(SignUpDTO.Request request) {
        log.info("AuthService.signUp called - ID : {} , nickname : {} ", request.id(), request.nickname());
        return ongiMemoUserRepository.existsByid(request.id())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new CustomException("이미 존재하는 아이디입니다."));
                    }

                    return duplicateEmail(request.email());
                })
                // 레디스 데이터 저장
                .flatMap(emailExists -> {
                    if (emailExists) {
                        return Mono.error(new CustomException("이미 사용중인 이메일입니다."));
                    }

                    String tempKey = Key.ONGI_SIGNUP_OBJ.formatted(request.email());
                    return redisUtil.setWithExpire(tempKey, request, Duration.ofMinutes(5));
                })
                // 이메일 발송
                .flatMap(saved -> {
                    if (!saved) {
                        return Mono.error(new CustomException("Redis save failed."));
                    }

                    return sendEmailVerifyCode(request.nickname(), request.email());
                })
                .flatMap(send -> {
                    if (!send) {
                        return Mono.error(new CustomException("이메일 전송에 실패했습니다."));
                    }

                    log.info("이메일 인증번호 전송 성공");
                    return Mono.just(SignUpDTO.Response.builder()
                                    .nickname(request.nickname())
                                    .id(request.id())
                                    .email(request.email())
                            .build());
                })
                .onErrorResume(error -> {
                    log.error("Error signUp - id: {}", request.id(), error);
                    return Mono.error(error);
                });
    }

    public Mono<SignUpCompleteDTO.Response> signUpComplete(ServerHttpRequest serverHttpRequest,SignUpCompleteDTO.Request request) {
        log.info("signUpComplete : {} " ,request.email());
        String redisKey = ONGI_EMAIL_VERIFY.formatted(request.email());
        return redisUtil.getString(redisKey)
                .switchIfEmpty(Mono.error(new CustomException("인증코드가 만료되었습니다. 다시 시도해주세요")))
                .map(code -> code.equals(request.verificationCode()))
                .flatMap(verified -> {
                    if (!verified) {
                        return Mono.error(new CustomException("인증코드가 올바르지 않습니다."));
                    }

                    String tempKey = Key.ONGI_SIGNUP_OBJ.formatted(request.email());
                    return redisUtil.getObject(tempKey, SignUpDTO.Request.class);
                })
                .flatMap(signUpRequest -> {
                    if (Util.CommonUtil.isEmpty(signUpRequest)) {
                        return Mono.error(new CustomException("임시 데이터가 만료되었습니다. 다시 시도해주세요."));
                    }

                    User user = User.toEntity(signUpRequest, passwordEncoder);
                    return ongiMemoUserRepository.save(user);
                })
                .flatMap(savedUser -> {
                    return createUserSession(serverHttpRequest, savedUser)
                            .flatMap(sessionKey -> {
                                return Mono.just(SignUpCompleteDTO.Response.builder()
                                        .userId(savedUser.getId())
                                        .success(true)
                                        .message("회원가입이 완료되었습니다.")
                                        .userId(savedUser.getId())
                                        .nickname(savedUser.getNickname())
                                        .sessionKey(sessionKey)
                                        .build());
                            });
                });

    }

    /**
     * 이메일 중복검사
     * @param email
     * @return
     */
    private Mono<Boolean> duplicateEmail(String email) {
        return ongiMemoUserRepository.existsByEmail(email);
    }

    /**
     * 메일 발송
     */
    private Mono<Boolean> sendEmailVerifyCode(String nickname, String email) {
        String randomCode = generateRandomCode(6);
        String redisKey = ONGI_EMAIL_VERIFY.formatted(email);

        return redisUtil.setString(redisKey, randomCode, Duration.ofMinutes(5))
                .flatMap(saved -> {
                    if (!saved) {
                        return Mono.just(false);
                    }

                    // 실제 이메일 발송 로직 (EmailService 사용)
                    boolean isSuccess = emailService.sendTemplateEmail(TemplateCode.SIGN_UP_VERIFY,
                                    List.of("온기메모",
                                            "온기메모"
                                            ,nickname
                                            , randomCode
                                            ,"OngiMemo"),
                                    email).block()
                            .isSuccess();

                    return Mono.just(isSuccess);
                });
    }

    private Mono<String> createUserSession(ServerHttpRequest request, User user) {
        // 세션 생성 로직 (기존 SessionService 활용)
        return sessionService.createSession(user.getEmail(), request)
                .flatMap(session -> {
                    return Mono.just(session.getSessionKey());
                });
    }


}
