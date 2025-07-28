package dev.gunho.api.bingous.v1.service;

import dev.gunho.api.bingous.v1.model.dto.SignInDto;
import dev.gunho.api.bingous.v1.model.dto.SignUpDto;
import dev.gunho.api.bingous.v1.model.entity.User;
import dev.gunho.api.bingous.v1.repository.UserRepository;
import dev.gunho.api.global.model.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;

//    public Mono<Result<SignInDto.Response>> signIn(SignInDto.Request request, ServerHttpRequest httpRequest) {
//        log.info("signIn - ID: {}", request.id());
//
//        return userRepository.findById(request.id())
//                .flatMap(user -> {
//                    // 비밀번호 검증
//                    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
//                        log.warn("Password mismatch - ID: {}", request.id());
//
//                        // 실패 횟수 증가
//                        return userRepository.incrementTryCnt(user.getId())
//                                .map(savedUser -> Result.<SignInDto.Response>failure("아이디 혹은 비밀번호를 확인해주세요", "LOGIN_FAILED"));
//                    }
//
//                    // 로그인 성공 - 마지막 로그인 시간 업데이트
//                    return sessionService.invalidateAllUserSessions(user.getId())
//                            .then(Mono.defer(() ->  sessionService.createSession(user.getId(), httpRequest)))
//                            .then(Mono.defer(() -> userRepository.updateLastLogin(user.getId())
//                                    .map(savedUser -> {
//                                        SignInDto.Response response = SignInDto.Response.builder()
//                                                .userId(user.getId())
//                                                .success(true)
//                                                .nickname(user.getNickname())
//                                                .message("로그인 성공")
//                                                .build();
//                                        return Result.success(response);
//                                    })));
//
//                })
//                .switchIfEmpty(Mono.just(Result.<SignInDto.Response>failure("사용자를 찾을 수 없습니다.", "USER_NOT_FOUND")))
//                .onErrorResume(error -> {
//                    log.error("Error in signIn - ID: {}", request.id(), error);
//                    return Mono.just(Result.<SignInDto.Response>failure("로그인 처리 중 오류가 발생했습니다.", "LOGIN_ERROR"));
//                });
//    }



    public Mono<Result<SignUpDto.Response>> signUp(SignUpDto.Request request, ServerHttpRequest httpRequest) {
        log.info("signUp - ID: {}, Email: {}", request.id(), request.email());

        return userRepository.existsById(request.id())
                .flatMap(idExists -> {
                    if (idExists) {
                        return Mono.just(Result.<SignUpDto.Response>failure("이미 사용 중인 ID입니다.", "DUPLICATE_ID"));
                    }

                    return userRepository.existsByEmail(request.email())
                            .flatMap(emailExists -> {
                                if (emailExists) {
                                    return Mono.just(Result.<SignUpDto.Response>failure("이미 사용 중인 이메일입니다.", "DUPLICATE_EMAIL"));
                                }

                                // 사용자 생성
                                User user = request.toEntity(passwordEncoder);
                                user.setNew(true);

                                return userRepository.save(user)
                                        .map(savedUser -> {
                                            savedUser.markNotNew();
                                            log.info("User created successfully - ID: {}", savedUser.getId());

                                            SignUpDto.Response responseData = SignUpDto.Response.builder()
                                                    .message("회원가입이 완료되었습니다.")
                                                    .userId(savedUser.getId())
                                                    .sessionKey(null) // 세션은 AuthService에서 처리
                                                    .success(true)
                                                    .build();

                                            return Result.success(responseData);
                                        });
                            });
                })
                .onErrorResume(error -> {
                    log.error("Error in signUp - ID: {}", request.id(), error);
                    return Mono.just(Result.<SignUpDto.Response>failure("회원가입 처리 중 오류가 발생했습니다.", "SIGNUP_ERROR"));
                });
    }

}
