package dev.gunho.api.bingous.v1.handler;

import dev.gunho.api.bingous.v1.model.dto.BudgetItemsDto;
import dev.gunho.api.bingous.v1.service.BudgetItemsService;
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
public class BudgetItemsHandler {

    private final RequestValidator requestValidator;
    private final BudgetItemsService budgetItemsService;

    /**
     * 가계부 생성
     */
    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(BudgetItemsDto.Request.class)
                .flatMap(requestValidator::validate)
                .flatMap(budgetItemsRequest ->
                        budgetItemsService.create(budgetItemsRequest, request.exchange().getRequest())
                )
                .flatMap(insertedItem -> {
                    ApiResponse<?> response = ApiResponse.success(insertedItem);
                    return ResponseHelper.ok(response);
                })
                .onErrorResume(ResponseHelper::handleException);
    }


    /**
     * 가계부 검색
     */
    public Mono<ServerResponse> search(ServerRequest request) {
        return budgetItemsService.searchMonth(request)
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
