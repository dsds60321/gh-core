package dev.gunho.api.bingous.v1.service;


import dev.gunho.api.bingous.v1.model.dto.ReflectionDto;
import dev.gunho.api.bingous.v1.model.dto.ScheduleDto;
import dev.gunho.api.bingous.v1.model.entity.Reflection;
import dev.gunho.api.bingous.v1.model.entity.Schedules;
import dev.gunho.api.bingous.v1.repository.ReflectionRepository;
import dev.gunho.api.bingous.v1.repository.ScheduleRepository;
import dev.gunho.api.bingous.v1.repository.UserRepository;
import dev.gunho.api.global.constants.CoreConstants;
import dev.gunho.api.global.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
@Slf4j
public class ReflectionService {


    private final UserRepository userRepository;
    private final ReflectionRepository reflectionRepository;

    public Mono<Boolean> create(ReflectionDto.Request request, ServerHttpRequest httpRequest) {
        String key = httpRequest.getHeaders().getFirst(CoreConstants.Network.AUTH_KEY);

        if (Util.CommonUtil.isEmpty(key)) {
            log.error("할일 등록 중 오류가 발생했습니다");
            return Mono.just(false);
        }

        return userRepository.findBySessionKey(key)
                .flatMap(user -> {

                    Reflection reflectionEntity = Reflection.createNew(request);

                    return reflectionRepository.save(reflectionEntity)
                            .map(scheduleEntity -> true);

                })
                .onErrorResume(error -> {
                    log.error("반성문 등록 중 오류가 발생했습니다: {}", error.getMessage());
                    return Mono.just(false);
                });

    }

    public Flux<Reflection> search(ServerRequest request) {
        String key = request.exchange().getRequest().getHeaders().getFirst(CoreConstants.Network.AUTH_KEY);
        if (Util.CommonUtil.isEmpty(key)) {
            log.error("반성문 검색 중 인증 오류가 발생했습니다");
            return Flux.error(new RuntimeException("인증 오류가 발생했습니다"));
        }

        return userRepository.findBySessionKey(key)
                .flatMapMany(user -> {
                    return reflectionRepository.findAllByCoupleId(user.getCoupleId());
                })
                .switchIfEmpty(Flux.empty());
    }
}
