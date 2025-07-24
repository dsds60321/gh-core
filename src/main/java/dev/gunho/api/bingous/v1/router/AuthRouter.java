package dev.gunho.api.bingous.v1.router;

import dev.gunho.api.bingous.v1.handler.AuthHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static dev.gunho.api.global.constants.CoreConstants.Host.*;

@Configuration
@RequiredArgsConstructor
public class AuthRouter {

    @Bean
    public RouterFunction<ServerResponse> signRoutes(AuthHandler authHandler) {
        return RouterFunctions.route()
                .path(V1_HOST, builder -> builder
                        .POST("/sign-up", authHandler::signUp)
                        .POST("/sign-in", authHandler::signIn)
                        .POST("/sign-up/email/verify", authHandler::verifyEmail)
                        .POST("/sign-up/email/confirm", authHandler::confirmEmail))
                .build();
    }
}
