package dev.gunho.api.bingous.v1.service;

import dev.gunho.api.bingous.v1.model.dto.AnniversaryDto;
import dev.gunho.api.bingous.v1.model.entity.Anniversary;
import dev.gunho.api.bingous.v1.model.entity.AppSession;
import dev.gunho.api.bingous.v1.repository.AnniversaryRepository;
import dev.gunho.api.bingous.v1.repository.AppSessionRepository;
import dev.gunho.api.bingous.v1.repository.UserRepository;
import dev.gunho.api.global.constants.CoreConstants;
import dev.gunho.api.global.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static dev.gunho.api.global.constants.CoreConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnniversaryService {

    private final UserRepository userRepository;
    private final AnniversaryRepository anniversaryRepository;

    public Mono<Boolean> create(AnniversaryDto.Request request, ServerHttpRequest httpRequest) {
        String key = httpRequest.getHeaders().getFirst(Network.AUTH_KEY);

        if (Util.CommonUtil.isEmpty(key)) {
            log.error("기념일 등록 중 오류가 발생했습니다");
            return Mono.just(false);
        }

        return userRepository.findBySessionKey(key)
                .flatMap(user -> {

                    Anniversary anniversary = request.toEntity().toBuilder()
                            .createdBy(user.getId())
                            .coupleId(user.getCoupleId())
                            .build();

                    return anniversaryRepository.save(anniversary)
                            .map(anniversarySaved -> true);

                })
                .onErrorResume(error -> {
                    log.error("기념일 등록 중 오류가 발생했습니다: {}", error.getMessage());
                    return Mono.just(false);
                });

    }

}
