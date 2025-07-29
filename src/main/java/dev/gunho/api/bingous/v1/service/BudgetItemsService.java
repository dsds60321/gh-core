package dev.gunho.api.bingous.v1.service;

import dev.gunho.api.bingous.v1.model.dto.BudgetItemsDto;
import dev.gunho.api.bingous.v1.repository.BudgetItemsRepository;
import dev.gunho.api.bingous.v1.repository.UserRepository;
import dev.gunho.api.global.constants.CoreConstants;
import dev.gunho.api.global.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetItemsService {

    private final UserRepository userRepository;
    private final BudgetItemsRepository budgetItemsRepository;

    public Mono<Boolean> create(BudgetItemsDto.Request request, ServerHttpRequest httpRequest) {
        String key = httpRequest.getHeaders().getFirst(CoreConstants.Network.AUTH_KEY);

        if (Util.CommonUtil.isEmpty(key)) {
            log.error("가계부 등록 중 오류가 발생했습니다");
            return Mono.just(false);
        }

        return userRepository.findBySessionKey(key)
                .flatMap(user -> {
                    LocalDateTime now = LocalDateTime.now();

                    // `Nested entities are not supported` 오류로 insert 쿼리 직접 할당
                    return budgetItemsRepository.insertBudgetItem(
                            user.getCoupleId(),
                            request.paidBy(),
                            request.title(),
                            request.description(),
                            request.location(),
                            request.amount(),
                            request.category().name(), // Enum은 name()을 사용해 String으로 변환
                            request.date(),
                            user.getId(),
                            now,
                            now
                    ).then(Mono.just(true)); // 성공하면 true 반환
                })
                .onErrorResume(error -> {
                    log.error("가계부 등록 중 오류가 발생했습니다: {}", error.getMessage());
                    return Mono.just(false);
                });
    }
}
