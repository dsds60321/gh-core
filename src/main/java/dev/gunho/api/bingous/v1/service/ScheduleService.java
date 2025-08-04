package dev.gunho.api.bingous.v1.service;

import dev.gunho.api.bingous.v1.model.dto.AnniversaryDto;
import dev.gunho.api.bingous.v1.model.dto.ScheduleDto;
import dev.gunho.api.bingous.v1.model.entity.Anniversary;
import dev.gunho.api.bingous.v1.model.entity.Schedules;
import dev.gunho.api.bingous.v1.repository.ScheduleRepository;
import dev.gunho.api.bingous.v1.repository.UserRepository;
import dev.gunho.api.global.constants.CoreConstants;
import dev.gunho.api.global.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;

    public Mono<Boolean> create(ScheduleDto.Request request, ServerHttpRequest httpRequest) {
        String key = httpRequest.getHeaders().getFirst(CoreConstants.Network.AUTH_KEY);

        if (Util.CommonUtil.isEmpty(key)) {
            log.error("할일 등록 중 오류가 발생했습니다");
            return Mono.just(false);
        }

        return userRepository.findBySessionKey(key)
                .flatMap(user -> {

                    Schedules schedules = Schedules.createNew(request, user.getCoupleId(), user.getId());

                    return scheduleRepository.save(schedules)
                            .map(scheduleEntity -> true);

                })
                .onErrorResume(error -> {
                    log.error("할일 등록 중 오류가 발생했습니다: {}", error.getMessage());
                    return Mono.just(false);
                });

    }

}
