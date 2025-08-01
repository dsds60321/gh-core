package dev.gunho.api.bingous.v1.router;

import dev.gunho.api.bingous.v1.handler.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static dev.gunho.api.global.constants.CoreConstants.Host.V1_HOST;

@Configuration
@RequiredArgsConstructor
public class BingoRouter {

    @Bean
    public RouterFunction<ServerResponse> authRoutes(AuthHandler authHandler) {
        return RouterFunctions.route()
                .path(V1_HOST, builder -> builder
                        .POST("/sign-up", authHandler::signUp)
                        .POST("/sign-in", authHandler::signIn)
                        .POST("/sign-up/email/verify", authHandler::verifyEmail)
                        .POST("/sign-up/email/confirm", authHandler::confirmEmail))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> dashboardRoutes(DashboardHandler dashboardHandler) {
        return RouterFunctions.route()
                .path(V1_HOST, builder -> builder
                        .GET("/dashboard", dashboardHandler::getDashboard))
                .build();
    }


    @Bean
    public RouterFunction<ServerResponse> coupleRoutes(CoupleHandler coupleHandler) {
        return RouterFunctions.route()
                .path(V1_HOST, builder -> builder
                        .GET("/couple/link", coupleHandler::createLink))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> anniversaryRoutes(AnniversaryHandler anniversaryHandler) {
        return RouterFunctions.route()
                .path(V1_HOST, builder -> builder
                        .POST("/anniversaries", anniversaryHandler::create)
                        .GET("/anniversaries", anniversaryHandler::search))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> budgetItemsRoutes(BudgetItemsHandler budgetItemsHandler) {
        return RouterFunctions.route()
                .path(V1_HOST, builder -> builder
                        .POST("/budget-items", budgetItemsHandler::create)
                        .GET("/budget-items", budgetItemsHandler::search))
                .build();
    }
}
