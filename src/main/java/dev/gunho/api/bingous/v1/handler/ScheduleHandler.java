package dev.gunho.api.bingous.v1.handler;

import dev.gunho.api.bingous.v1.model.dto.AnniversaryDto;
import dev.gunho.api.bingous.v1.model.dto.ScheduleDto;
import dev.gunho.api.bingous.v1.service.ScheduleService;
import dev.gunho.api.global.model.dto.ApiResponse;
import dev.gunho.api.global.util.RequestValidator;
import dev.gunho.api.global.util.ResponseHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleHandler {

    private final ScheduleService scheduleService;
    private final RequestValidator requestValidator;

    /**
     * 할일 생성
     */
    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(ScheduleDto.Request.class)
                .flatMap(requestValidator::validate)
                .flatMap(scheduleRequest ->
                        scheduleService.create(scheduleRequest, request.exchange().getRequest())
                )
                .flatMap(isCreate -> {
                    ApiResponse<?> response;
                    if (isCreate) {
                        response = ApiResponse.success("할일 등록에 성공 했습니다.");
                    } else {
                        response = ApiResponse.failure("할일 등록에 실패 했습니다.");
                    }

                    return ResponseHelper.ok(response);
                })
                .onErrorResume(ResponseHelper::handleException);
    }
}
