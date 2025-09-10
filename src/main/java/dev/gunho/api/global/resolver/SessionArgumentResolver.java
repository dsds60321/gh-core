package dev.gunho.api.global.resolver;

import dev.gunho.api.global.annotation.GhSession;
import dev.gunho.api.global.model.dto.SessionDto;
import org.springframework.core.MethodParameter;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class SessionArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String SESSION_PRINCIPAL = "sessionPrincipal";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(GhSession.class) && parameter.getParameterType().equals(SessionDto.class);
    }

    @Override
    public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {
        Object principal = exchange.getAttribute(SESSION_PRINCIPAL);
        return Mono.justOrEmpty(principal);
    }

}
