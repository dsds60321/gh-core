package dev.gunho.api.bingous.v1.handler;

import dev.gunho.api.bingous.v1.service.CoupleService;
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
public class CoupleHandler {

    private final CoupleService coupleService;
    private final RequestValidator requestValidator;

    /**
     * 링크 생성
     */
    public Mono<ServerResponse> createLink(ServerRequest request) {
        return coupleService.createInviteLink(request.exchange().getRequest())
                .flatMap(link -> {
                    ApiResponse<?> response;

                    if (link == null) {
                        response = ApiResponse.failure("링크 생성에 실패했습니다.");
                    }  else {
                        response = ApiResponse.success(link);
                    }

                    return ResponseHelper.ok(response);
                })
                .onErrorResume(ResponseHelper::handleException);
    }

}
