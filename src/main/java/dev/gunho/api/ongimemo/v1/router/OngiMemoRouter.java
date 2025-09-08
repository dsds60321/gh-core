package dev.gunho.api.ongimemo.v1.router;

import dev.gunho.api.ongimemo.v1.handler.AuthHandler;
import dev.gunho.api.ongimemo.v1.handler.DashboardHandler;
import dev.gunho.api.ongimemo.v1.handler.ReflectionHandler;
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
                      .POST("/request-sign-up", authHandler::signUpStep1)
                      .POST("/complete-sign-up", authHandler::signUpStep2))
              .build();
    }

    @Bean(name = "ongiMemoDashboardRoutes")
    RouterFunction<ServerResponse> dashboardRoutes(DashboardHandler dashboardHandler) {
        return RouterFunctions.route()
                .path(V1_ONGI_HOST, builder -> builder
                        .POST("/dashboard", dashboardHandler::getDashboard))
                .build();
    }

    @Bean(name = "ongiMemoReflectionRoutes")
    RouterFunction<ServerResponse> reflectionRoutes(ReflectionHandler reflectionHandler) {
        return RouterFunctions.route()
                .path(V1_ONGI_HOST, builder -> builder
                        .GET("/reflections", reflectionHandler::getAllByCreatedAt))
                .build();
    }
}
