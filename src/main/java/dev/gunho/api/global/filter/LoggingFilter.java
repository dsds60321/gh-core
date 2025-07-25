package dev.gunho.api.global.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * 로깅용 filter
 * TODO : 추후 요청에 대한 기록
 */
@Component
@Slf4j
public class LoggingFilter implements WebFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 각 요청에 고유 ID 생성
        String requestId = UUID.randomUUID().toString();

        // 시작 시간 기록
        long startTime = System.currentTimeMillis();

        // 요청 정보 로깅
        ServerHttpRequest request = exchange.getRequest();
        log.info("[{}] 요청 시작: {} {} (ContentLength: {})",
                requestId,
                request.getMethod(),
                request.getURI(),
                request.getHeaders().getContentLength());

        // 요청 헤더 로깅 (필요한 경우)
        if (log.isDebugEnabled()) {
            log.debug("[{}] Headers: {}", requestId, request.getHeaders());
        }

        // 응답 필터를 추가하여 응답 정보도 로깅
        return chain.filter(exchange)
                .doOnSuccess(v -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("[{}] 요청 완료: {} {} - {} ({}ms)",
                            requestId,
                            request.getMethod(),
                            request.getURI(),
                            exchange.getResponse().getStatusCode(),
                            duration);
                })
                .doOnError(err -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("[{}] 요청 실패: {} {} ({}ms) - 오류: {}",
                            requestId,
                            request.getMethod(),
                            request.getURI(),
                            duration,
                            err.getMessage());
                });
    }

    @Override
    public int getOrder() {
        // 가장 먼저 실행되도록 높은 우선순위 부여
        return Ordered.HIGHEST_PRECEDENCE;
    }
}