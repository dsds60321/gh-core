package dev.gunho.api.ongimemo.v1.handler;

import dev.gunho.api.global.model.dto.ApiResponse;
import dev.gunho.api.global.util.RequestValidator;
import dev.gunho.api.global.util.ResponseHelper;
import dev.gunho.api.ongimemo.v1.model.dto.DashboardDTO;
import dev.gunho.api.ongimemo.v1.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardHandler {

    private final DashboardService dashboardService;

    public Mono<ServerResponse> getDashboard(ServerRequest request) {
        log.info("DashboardHandler.getDashboard called");
        return dashboardService.dashboardData(request)
                .flatMap(body -> ResponseHelper.ok(ApiResponse.success(body)))
                .onErrorResume(ResponseHelper::handleException);
    }
}
