package dev.gunho.api.ongimemo.v1.router;

import dev.gunho.api.global.annotation.GhSession;
import dev.gunho.api.global.model.dto.SessionDto;
import dev.gunho.api.ongimemo.v1.handler.*;
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
                        .GET("/dashboard", dashboardHandler::getDashboard))
                .build();
    }

    @Bean(name = "ongiMemoReflectionRoutes")
    RouterFunction<ServerResponse> reflectionRoutes(ReflectionHandler reflectionHandler) {
        return RouterFunctions.route()
                .path(V1_ONGI_HOST, builder -> builder
                        .GET("/reflections", reflectionHandler::getAllByCreatedAt)
                        .POST("/reflections", reflectionHandler::createReflection))
                .build();
    }

    @Bean(name = "ongiMemoPraiseRoutes")
    RouterFunction<ServerResponse> praiseRoutes(PraiseHandler praiseHandler) {
        return RouterFunctions.route()
                .path(V1_ONGI_HOST, builder -> builder
                        .GET("/praises", praiseHandler::getAllByCreatedAt)
                        .POST("/praises", praiseHandler::createPraise))
                .build();
    }

    @Bean(name = "ongiMemoFriendsRoutes")
    RouterFunction<ServerResponse> friendsRoutes(FriendsHandler friendsHandler) {
        return RouterFunctions.route()
                .path(V1_ONGI_HOST, builder -> builder
                        .GET("/friends", friendsHandler::getFriends)
                        .POST("/friends/invite", friendsHandler::inviteFriend))
                .build();
    }
}
