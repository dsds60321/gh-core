package dev.gunho.api.global.filter;

import dev.gunho.api.bingous.v1.service.SessionService;
import dev.gunho.api.global.annotation.GhSession;
import dev.gunho.api.global.constants.CoreConstants;
import dev.gunho.api.global.model.dto.SessionDto;
import dev.gunho.api.global.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionValidationFilter implements WebFilter {

    private final SessionService sessionService;
    private final RedisTemplate<Object, Object> redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        log.debug("path: {}", path);
        // 인증이 필요하지 않은 경로들
        if (isPublicPath(path)) {
            log.debug("Public path: {}", path);
            return chain.filter(exchange);
        }

        // Authorization 헤더에서 세션 키 추출
        String sessionKey = exchange.getRequest().getHeaders().getFirst(CoreConstants.Network.AUTH_KEY);

        // 디버그
        exchange.getRequest().getHeaders().forEach((name, values) -> {
            log.debug("header: {} => {}", name, values);
        });

        if (Util.CommonUtil.isEmpty(sessionKey)) {
            log.warn("Session key is empty for path: {}", path);
            return unauthorizedResponse(exchange);
        }

        // 세션 검증
        return sessionService.validateSession(sessionKey)
                .flatMap(session -> {
                    SessionDto sessionDto = SessionDto.from(session); // 어노테이션용 session 생성

                    // 요청에 사용자 정보 추가 (다른 서비스에서 사용할 수 있도록)
                    exchange.getAttributes().put("userId", session.getUserId());
                    exchange.getAttributes().put("sessionKey", sessionKey);
                    exchange.getAttributes().put("sessionId", session.getId());
                    return chain.filter(exchange)
                            .contextWrite(ctx -> ctx.put(GhSession.class, sessionDto));
                })
                .onErrorResume(error -> {
                    log.warn("Session validation failed for key: {}", sessionKey, error);
                    return unauthorizedResponse(exchange);
                });
    }

    private boolean isPublicPath(String path) {
        String[] publicSuffixes = new String[] {
                "/sign-up",
                "/sign-in",
                "/sign-up/email/verify",
                "/sign-up/email/confirm",
                "/request-sign-up",
                "/complete-sign-up"
        };


        for (String hostPrefix : CoreConstants.Host.V1_HOSTS) {
            for (String suffix : publicSuffixes) {
                if (path.startsWith(hostPrefix + suffix)) {
                    return true;
                }
            }
        }
        return false;

    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
