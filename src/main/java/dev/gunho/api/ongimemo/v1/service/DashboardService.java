package dev.gunho.api.ongimemo.v1.service;

import dev.gunho.api.ongimemo.v1.model.dto.DashboardDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class DashboardService {

    private final ReflectionService reflectionService;
    private final PraiseService praiseService;

    public Mono<DashboardDTO.Response> dashboardData(ServerRequest request) {
        log.info("DashboardService - dashboardData called");
        return
                Mono.zip(
                                reflectionService.search(request)
                                        .collectList(),
                                praiseService.search(request)
                                        .collectList()
                        )
                        .map(tuple -> new DashboardDTO.Response(
                                tuple.getT1(), // reflectionList
                                tuple.getT2()  // praiseList
                        ));

    }
}
