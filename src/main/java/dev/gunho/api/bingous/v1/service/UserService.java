package dev.gunho.api.bingous.v1.service;

import dev.gunho.api.bingous.v1.model.dto.SignUp;
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
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;

    public Mono<Result<SignUp.Response>> signUp(SignUp.Request request, ServerHttpRequest httpRequest) {
        log.info("signUp - ID: {}, Email: {}", request.id(), request.email());

        return userRepository.existsById(request.id())
                .flatMap(idExists -> {
                    if (idExists) {
                        return Mono.just(Result.<SignUp.Response>failure("이미 사용 중인 ID입니다.", "DUPLICATE_ID"));
                    }

                    return userRepository.existsByEmail(request.email())
                            .flatMap(emailExists -> {
                                if (emailExists) {
                                    return Mono.just(Result.<SignUp.Response>failure("이미 사용 중인 이메일입니다.", "DUPLICATE_EMAIL"));
                                }

                                // 사용자 생성
                                User user = request.toEntity(passwordEncoder);
                                user.setNew(true);

                                return userRepository.save(user)
                                        .flatMap(savedUser -> {
                                            savedUser.markNotNew();
                                            log.info("User created successfully - ID: {}", savedUser.getId());

                                            // 세션 생성
                                            return sessionService.createSession(savedUser.getId(), httpRequest)
                                                    .map(session -> {
                                                        SignUp.Response responseData = SignUp.Response.builder()
                                                                .message("회원가입이 완료되었습니다.")
                                                                .userId(savedUser.getId())
                                                                .sessionKey(session.getSessionKey()) // 세션 키 추가
                                                                .success(true)
                                                                .build();

                                                        return Result.success(responseData);
                                                    });
                                        });
                            });
                })
                .onErrorResume(error -> {
                    log.error("Error in signUp - ID: {}", request.id(), error);
                    return Mono.just(Result.failure("회원가입 처리 중 오류가 발생했습니다.", "SIGNUP_ERROR"));
                });
    }
}
