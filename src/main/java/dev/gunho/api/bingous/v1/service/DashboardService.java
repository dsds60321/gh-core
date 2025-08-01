package dev.gunho.api.bingous.v1.service;

import dev.gunho.api.bingous.v1.model.dto.DashboardDto;
import dev.gunho.api.bingous.v1.repository.AnniversaryRepository;
import dev.gunho.api.bingous.v1.repository.CoupleRepository;
import dev.gunho.api.bingous.v1.repository.ScheduleRepository;
import dev.gunho.api.bingous.v1.repository.UserRepository;
import dev.gunho.api.global.constants.CoreConstants;
import dev.gunho.api.global.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AnniversaryRepository anniversaryRepository;
    private final ScheduleRepository scheduleRepository;
    private final CoupleRepository coupleRepository;
    private final UserRepository userRepository;

    public Mono<DashboardDto.Response> getDashboardData(ServerRequest request) {

        String sessionKey = request.headers().firstHeader(CoreConstants.Network.AUTH_KEY);

        if (Util.CommonUtil.isEmpty(sessionKey)) {
            return Mono.just(DashboardDto.Response.builder().build());
        }

        return userRepository.findBySessionKey(sessionKey)
                .flatMap(user -> {
                    Long coupleId = user.getCoupleId();
                    log.info("getDashboardData called - CoupleId: {}", coupleId);

                    return Mono.zip(getAnniversaries(coupleId),
                                    getSchedules(coupleId))
                            .map(tuple -> DashboardDto.Response.builder()
                                    .anniversaries(tuple.getT1())
                                    .schedules(tuple.getT2())
                                    .build());
                });
    }

    private Mono<List<DashboardDto.AnniversaryPayload>> getAnniversaries(Long coupleId) {
        return anniversaryRepository.findAllByCoupleId(coupleId)
                .map(DashboardDto::toAnniversary)
                .collectList();
    }

    private Mono<List<DashboardDto.SchedulePayload>> getSchedules(Long coupleId) {
        return scheduleRepository.findAllByCoupleId(coupleId)
                .map(DashboardDto::toSchedule)
                .collectList();
    }
}
