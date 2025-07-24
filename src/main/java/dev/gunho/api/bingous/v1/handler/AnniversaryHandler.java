package dev.gunho.api.bingous.v1.handler;

import dev.gunho.api.bingous.v1.model.dto.AnniversaryDto;
import dev.gunho.api.bingous.v1.service.AnniversaryService;
import dev.gunho.api.global.util.RequestValidator;
import dev.gunho.api.global.util.ResponseHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(AnniversaryDto.Request.class)
                .flatMap(requestValidator::validate)
                .flatMap(anniversaryService::create)
                .flatMap(isCreate -> {
                    if (isCreate) {
                        return ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(isCreate);
                    } else {
                        return ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(isCreate);
                    }
                })
                .onErrorResume(ResponseHelper::handleException);
    }
}
