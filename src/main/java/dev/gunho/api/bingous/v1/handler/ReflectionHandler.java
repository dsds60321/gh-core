package dev.gunho.api.bingous.v1.handler;

import dev.gunho.api.bingous.v1.model.dto.ReflectionDto;
import dev.gunho.api.bingous.v1.model.dto.ScheduleDto;
import dev.gunho.api.bingous.v1.service.ReflectionService;
import dev.gunho.api.bingous.v1.service.ScheduleService;
import dev.gunho.api.global.model.dto.ApiResponse;
import dev.gunho.api.global.util.RequestValidator;
import dev.gunho.api.global.util.ResponseHelper;
import dev.gunho.api.global.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReflectionHandler {

    private final ReflectionService reflectionService;
    private final RequestValidator requestValidator;

    /**
     * 할일 생성
     */
    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(ReflectionDto.Request.class)
                .flatMap(requestValidator::validate)
                .flatMap(scheduleRequest ->
                        reflectionService.create(scheduleRequest, request.exchange().getRequest())
                )
                .flatMap(isCreate -> {
                    ApiResponse<?> response;
                    if (isCreate) {
                        response = ApiResponse.success("반성문 등록에 성공 했습니다.");
                    } else {
                        response = ApiResponse.failure("반성문 등록에 실패 했습니다.");
                    }

                    return ResponseHelper.ok(response);
                })
                .onErrorResume(ResponseHelper::handleException);
    }

    /**
     * 반성문 상태 업데이트 (승인/반려)
     */
    public Mono<ServerResponse> updateStatus(ServerRequest request) {
        return request.bodyToMono(ReflectionDto.StatusUpdate.class)
                .flatMap(requestValidator::validate)
                .flatMap(statusRequest -> {
                    Long reflectionId = Long.valueOf(request.pathVariable("reflectionId"));
                    return reflectionService.updateStatus(reflectionId, statusRequest, request.exchange().getRequest());
                })
                .flatMap(result -> {
                    ApiResponse<?> response;
                    if (result) {
                        response = ApiResponse.success("반성문 상태가 업데이트되었습니다.");
                    } else {
                        response = ApiResponse.failure("반성문 상태 업데이트에 실패했습니다.");
                    }
                    return ResponseHelper.ok(response);
                })
                .onErrorResume(ResponseHelper::handleException);
    }


    public Mono<ServerResponse> search(ServerRequest request) {
        return reflectionService.search(request)
                .collectList()
                .flatMap(result -> {
                    ApiResponse<?> response;
                    if (Util.CommonUtil.isNotEmpty(result) ) {
                        response = ApiResponse.success(result);
                    } else {
                        response = ApiResponse.success("조회된 결과가 없습니다.");
                    }
                    return ResponseHelper.ok(response);
                })
                .onErrorResume(ResponseHelper::handleException);
    }
}
