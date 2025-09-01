package dev.gunho.api.ongimemo.v1.service;

import dev.gunho.api.global.exception.CustomException;
import dev.gunho.api.ongimemo.v1.model.dto.SignUpDTO;
import dev.gunho.api.ongimemo.v1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    /**
     * 회원가입
     */
    public Mono<SignUpDTO.Response> signUp(SignUpDTO.Request request) {
        log.info("AuthService.signUp called - ID : {} , nickname : {} ", request.id(), request.nickname());
        return userRepository.existsById(request.id())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new CustomException("이미 존재하는 아이디입니다."));
                    }

                    return Mono.just(SignUpDTO.Response.builder()
                            .id(request.id())
                            .email(request.email())
                            .nickname(request.nickname())
                            .token("token")
                            .build());
                });
    }
}
