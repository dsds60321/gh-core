package dev.gunho.api.bingous.v1.service;

import dev.gunho.api.bingous.v1.model.dto.EmailVerifyDto;
import dev.gunho.api.bingous.v1.model.dto.SignInDto;
import dev.gunho.api.bingous.v1.model.dto.SignUpDto;
import dev.gunho.api.bingous.v1.model.entity.Couples;
import dev.gunho.api.bingous.v1.model.entity.User;
import dev.gunho.api.bingous.v1.repository.CoupleRepository;
import dev.gunho.api.bingous.v1.repository.InviteTokenRepository;
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

import static dev.gunho.api.global.constants.CoreConstants.Key.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmailService emailService;
    private final RedisUtil redisUtil;
    private final SessionService sessionService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CoupleRepository coupleRepository;
    private final InviteTokenRepository inviteTokenRepository;

    /**
     * 이메일 인증코드 발송
     */
    public Mono<Boolean> sendEmailVerifyCode(EmailVerifyDto.Request request) {
        String randomCode = Util.CommonUtil.generateRandomCode(6);
        String redisKey = EMAIL_VERIFY.formatted(request.email());

        log.info("sendEmailVerifyCode - ID: {}, Email: {}", request.id(), request.email());

        return redisUtil.setString(redisKey, randomCode, Duration.ofMinutes(5))
                .flatMap(saved -> {
                    log.info("Email verify code saved successfully - Email: {}", request.email());
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
                    log.info("Error sendEmailVerifyCode - Email: {}", request.email(), error);
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
                .message("인증 코드가 올바르지 않습니다.")
                .build();


        return redisUtil.getString(EMAIL_VERIFY.formatted(request.email()))
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
                .switchIfEmpty(Mono.just(response))
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

        return userRepository.existsById(request.id())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.just(SignUpDto.Response.builder()
                                .success(false)
                                .message("이미 사용중인 아이디입니다.")
                                .build());
                    }

                    User user = request.toEntity(passwordEncoder);
                    user.setNew(true);

                    return userRepository.save(user)
                            .flatMap(savedUser -> {
                                savedUser.markNotNew();

                                // 토큰이 있으면 커플 등록 처리
                                if (Util.CommonUtil.isNotEmpty(request.token())) {
                                    return processCoupleRegistration(request.token(), savedUser.getId());
                                }

                                log.info("User created successfully - ID: {}", savedUser.getId());
                                // 일반 회원가입
                                return Mono.just(SignUpDto.Response.builder()
                                        .message("회원가입이 완료되었습니다.")
                                        .userId(savedUser.getId())
                                        .success(true)
                                        .build());
                            });
                })
                .onErrorResume(error -> {
                    log.error("Error signUp - id: {}", request.id(), error);
                    return Mono.just(SignUpDto.Response.builder()
                            .message("회원가입 중 오류가 발생했습니다.")
                            .success(false)
                            .build());
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

                    // 커플정보가 없는 경우
                    if (Util.CommonUtil.isEmpty(user.getCoupleId())) {
                        return sessionService.invalidateAllUserSessions(user.getId())
                                .then(Mono.defer(() -> userRepository.updateLastLogin(user.getId())))
                                .then(Mono.defer(() -> sessionService.createSession(user.getId(), httpRequest)))
                                .map(session -> response.toBuilder()
                                        .sessionKey(session.getSessionKey())
                                        .user(SignInDto.toUserPayload(user))
                                        .couple(null)
                                        .success(true)
                                        .nickname(user.getNickname())
                                        .message("로그인에 성공했습니다")
                                        .build());
                    }

                    // 커플 정보 조회
                    return coupleRepository.findById(user.getCoupleId())
                            .flatMap(couples -> {
                                // 커플 정보가 있는 경우
                                return sessionService.invalidateAllUserSessions(user.getId())
                                        .then(Mono.defer(() -> userRepository.updateLastLogin(user.getId())))
                                        .then(Mono.defer(() -> sessionService.createSession(user.getId(), httpRequest)))
                                        .map(session -> response.toBuilder()
                                                .sessionKey(session.getSessionKey())
                                                .user(SignInDto.toUserPayload(user))
                                                .couple(SignInDto.toCouplePayload(couples))
                                                .success(true)
                                                .nickname(user.getNickname())
                                                .message("로그인에 성공했습니다")
                                                .build());
                            });
                })
                .switchIfEmpty(Mono.just(response))
                .onErrorResume(error -> {
                    log.error("Error in signIn - ID: {}", request.id(), error);
                    return Mono.just(response);
                });
    }


    /**
     * 커플 등록 처리 (별도 메서드로 분리)
     */
    private Mono<SignUpDto.Response> processCoupleRegistration(String token, String userId) {
        String redisKey = COUPLE_INVITE.formatted(token);

        return redisUtil.getString(redisKey)
                .flatMap(inviterUserId -> {
                    if (inviterUserId == null) {
                        return Mono.just(createResponse(userId, "회원가입은 성공했으나 초대 링크가 만료되었습니다.", true));
                    }

                    return inviteTokenRepository.updateInviteeUser(token, userId)
                            .flatMap(updateCount -> {
                                if (updateCount <= 0) {
                                    log.error("Couple registration failed - Token: {}, InviteeId: {}", token, userId);
                                    return Mono.just(createResponse(userId, "회원가입에 성공했으나 커플 등록에 실패했습니다.", true));
                                }

                                log.info("Couple registration successful - Token: {}, InviteeId: {}", token, userId);

                                // couple 등록
                                Couples couples = Couples.toEntity(inviterUserId, userId);
                                return coupleRepository.save(couples)
                                        .flatMap(savedCouples -> {
                                            log.info("Couple saved successfully - ID: {}", savedCouples.getId());
                                            log.info("User CoupleIdx update - InviterId: {}, InviteeId: {}", inviterUserId, userId);

                                            // userIdx 업데이트
                                            List<String> userIds = List.of(inviterUserId, userId);
                                            return userRepository.updateCoupleFk(savedCouples.getId(), userIds)
                                                    .map(coupleIdxUpdateCount -> {
                                                        if (coupleIdxUpdateCount > 0) {
                                                            return createResponse(userId, "회원가입 및 커플 등록이 완료되었습니다.", true);
                                                        } else {
                                                            return createResponse(userId, "회원가입은 성공했으나 커플 등록에 실패했습니다.", false);
                                                        }
                                                    });
                                        });
                            });
                })
                .switchIfEmpty(Mono.just(createResponse(userId, "회원가입은 성공했으나 초대 정보를 찾을 수 없습니다.", true)))
                .onErrorResume(error -> {
                    log.error("Error in processCoupleRegistration - Token: {}, UserId: {}", token, userId, error);
                    return Mono.just(createResponse(userId, "회원가입에 성공했으나 커플 등록에 실패했습니다.", true));
                });
    }


    /**
     * Response 생성 헬퍼 메서드
     */
    private SignUpDto.Response createResponse(String userId, String message, boolean success) {
        return SignUpDto.Response.builder()
                .userId(userId)
                .message(message)
                .success(success)
                .build();
    }

}
