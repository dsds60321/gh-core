package dev.gunho.api.bingous.v1.service;

import dev.gunho.api.bingous.v1.model.dto.EmailVerifyDto;
import dev.gunho.api.bingous.v1.model.dto.InviteDto;
import dev.gunho.api.bingous.v1.model.dto.SignInDto;
import dev.gunho.api.bingous.v1.model.dto.SignUpDto;
import dev.gunho.api.bingous.v1.model.entity.User;
import dev.gunho.api.bingous.v1.repository.UserRepository;
import dev.gunho.api.global.constants.CoreConstants;
import dev.gunho.api.global.enums.TemplateCode;
import dev.gunho.api.global.service.EmailService;
import dev.gunho.api.global.util.RedisUtil;
import dev.gunho.api.global.util.Util;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final SessionService sessionService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 이메일 인증코드 발송
     */
    public Mono<Boolean> sendEmailVerifyCode(EmailVerifyDto.Request request) {
        String randomCode = Util.CommonUtil.generateRandomCode(6);
        String redisKey = CoreConstants.Key.EMAIL_VERIFY.formatted(request.email());

        log.info("sendEmailVerifyCode - ID: {}, Email: {}", request.id(), request.email());

        return redisUtil.setString(redisKey, randomCode, Duration.ofMinutes(5))
                .flatMap(saved -> {
                    if (!saved) {
                        log.error("Redis save failed - Email: {}", request.email());
                        return Mono.just(false);
                    }

                    return emailService.sendTemplateEmail(
                            TemplateCode.SIGN_UP_VERIFY,
                            List.of(request.id(), randomCode),
                            request.email()
                    ).flatMap(response -> {
                        return Mono.just(response.isSuccess());
                    });
                })
                .onErrorResume(error -> {
                    log.error("Error sendEmailVerifyCode - Email: {}", request.email(), error);
                    return Mono.just(false);
                });
    }

    /**
     * 이메일 인증코드 확인
     */
    public Mono<EmailVerifyDto.VerifyCodeResponse> verifyEmailCode(EmailVerifyDto.VerifyCodeRequest request) {
        log.info("verifyEmailCode - Email: {}, Code: {}", request.email(), request.code());

        EmailVerifyDto.VerifyCodeResponse response = EmailVerifyDto.VerifyCodeResponse
                .builder()
                .verified(false)
                .message("이메일 인증에 실패했습니다.")
                .build();

        return redisUtil.getString(request.code())
                .flatMap(code -> {
                    if (StringUtil.isNullOrEmpty(code)) {
                        return Mono.just(response);
                    }

                    if (request.code().equalsIgnoreCase(code)) {
                        return Mono.just(response.toBuilder()
                                .verified(true)
                                .message("인증에 성공했습니다.")
                                .build());
                    }

                    return Mono.just(response);
                })
                .onErrorResume(error -> {
                    log.error("Error verifyEmailCode - Email: {}", request.email(), error);
                    return Mono.just(response);
                });
    }

    /**
     * 회원가입
     */
    public Mono<SignUpDto.Response> signUp(SignUpDto.Request request) {
        log.info("signUp called - ID: {}, Email: {}", request.id(), request.email());
        SignUpDto.Response response = SignUpDto.Response
                .builder()
                .success(false)
                .message("이미 사용중인 아이디입니다.")
                .build();

        return userRepository.existsById(request.id())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.just(response);
                    }

                    User user = request.toEntity(passwordEncoder);
                    user.setNew(true);

                    return userRepository.save(user)
                            .map(savedUser -> {
                                savedUser.markNotNew();
                                log.info("User created successfully - ID: {}", savedUser.getId());

                                return response.toBuilder()
                                        .message("회원가입이 완료되었습니다.")
                                        .userId(savedUser.getId())
                                        .success(true)
                                        .build();
                            });
                })
                .onErrorResume(error -> {
                    log.error("Error signUp - id: {}", request.id(), error);
                    return Mono.just(response);
                });
    }

    /**
     * 로그인
     */
    public Mono<SignInDto.Response> signIn(SignInDto.Request request, ServerHttpRequest httpRequest) {
        log.info("signIn called - ID: {}", request.id());
        SignInDto.Response response = SignInDto.Response.builder()
                .success(false)
                .message("아이디 혹은 비밀번호가 맞지 않습니다.")
                .build();

        return userRepository.findById(request.id())
                .flatMap(user -> {
                    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
                        log.warn("Password mismatch - ID: {}", request.id());

                        return userRepository.incrementTryCnt(request.id())
                                .map(udtCnt -> response);
                    }

                    return sessionService.invalidateAllUserSessions(user.getId())
                            .then(Mono.defer(() -> userRepository.updateLastLogin(user.getId())))
                            .then(Mono.defer(() ->  sessionService.createSession(user.getId(), httpRequest)))
                            .map(session -> response.toBuilder()
                                    .sessionKey(session.getSessionKey())
                                    .userId(user.getId())
                                    .success(true)
                                    .nickname(user.getNickname())
                                    .message("로그인에 성공했습니다")
                                    .build());
                })
                .switchIfEmpty(Mono.just(response))
                .onErrorResume(error -> {
                    log.error("Error in signIn - ID: {}", request.id(), error);
                    return Mono.just(response);
                });
    }

    public Mono<String> inviteCouple(InviteDto.Request request) {
        return userRepository.findById(request.id())
                .flatMap(user -> {





                })
                .switchIfEmpty(Mono.just(null))
                .onErrorResume(error -> {
                    log.error("Error in inviteCouple - ID: {}", request.id(), error);
                    return Mono.just(null);
                });
    }
}
