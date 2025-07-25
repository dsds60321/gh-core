package dev.gunho.api.global.filter;

import dev.gunho.api.bingous.v1.service.SessionService;
import dev.gunho.api.global.constants.CoreConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static dev.gunho.api.global.constants.CoreConstants.Host.V1_HOST;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionValidationFilter implements WebFilter {

    private final SessionService sessionService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();

        // 인증이 필요하지 않은 경로들
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        // Authorization 헤더에서 세션 키 추출
        String sessionKey = exchange.getRequest().getHeaders().getFirst(CoreConstants.Network.AUTH_KEY);
        if (sessionKey == null || sessionKey.isEmpty()) {
            return unauthorizedResponse(exchange);
        }

        // 세션 검증
        return sessionService.validateSession(sessionKey)
                .flatMap(session -> {
                    // 요청에 사용자 정보 추가 (다른 서비스에서 사용할 수 있도록)
                    exchange.getAttributes().put("userId", session.getUserId());
                    exchange.getAttributes().put("sessionKey", sessionKey); // ✅ 이제 정상 작동!
                    exchange.getAttributes().put("sessionId", session.getId());
                    return chain.filter(exchange);
                })
                .onErrorResume(error -> {
                    log.warn("Session validation failed for key: {}", sessionKey, error);
                    return unauthorizedResponse(exchange);
                });
    }

    private boolean isPublicPath(String path) {
        return path.startsWith(V1_HOST + "/sign-up") ||
                path.startsWith(V1_HOST + "/sign-in") ||
                path.startsWith(V1_HOST + "/sign-up/email/verify") ||
                path.startsWith(V1_HOST + "/sign-up/email/confirm");
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
