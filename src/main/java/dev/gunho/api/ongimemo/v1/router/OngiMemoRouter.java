package dev.gunho.api.ongimemo.v1.router;

import dev.gunho.api.ongimemo.v1.handler.AuthHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static dev.gunho.api.global.constants.CoreConstants.Host.V1_ONGI_HOST;

@Configuration
@RequiredArgsConstructor
public class OngiMemoRouter {

    @Bean(name = "ongiMemoAuthRoutes")
    RouterFunction<ServerResponse> authRoutes(AuthHandler authHandler) {
      return RouterFunctions.route()
              .path(V1_ONGI_HOST, builder -> builder
                      .POST("/sign-up", authHandler::signUp))
              .build();
    }
}
