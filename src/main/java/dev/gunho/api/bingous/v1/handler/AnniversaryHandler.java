package dev.gunho.api.bingous.v1.handler;

import dev.gunho.api.bingous.v1.model.dto.AnniversaryDto;
import dev.gunho.api.bingous.v1.service.AnniversaryService;
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
public class AnniversaryHandler {

    private final AnniversaryService anniversaryService;
    private final RequestValidator requestValidator;

    /**
     * 기념일 생성
     */
    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(AnniversaryDto.Request.class)
                .flatMap(requestValidator::validate)
                .flatMap(anniversaryRequest ->
                    anniversaryService.create(anniversaryRequest, request.exchange().getRequest())
                )
                .flatMap(isCreate -> {
                    ApiResponse<?> response;
                    if (isCreate) {
                        response = ApiResponse.success("기념일 등록에 성공 했습니다.");
                    } else {
                        response = ApiResponse.failure("기념일 등록에 실패 했습니다.");
                    }

                    return ResponseHelper.ok(response);
                })
                .onErrorResume(ResponseHelper::handleException);
    }

    /**
     * 기념일 검색
     */
    public Mono<ServerResponse> search(ServerRequest request) {
        return anniversaryService.searchAnniversaries(request)
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
