package dev.gunho.api.bingous.v1.service;

import dev.gunho.api.bingous.v1.model.dto.BudgetItemsDto;
import dev.gunho.api.bingous.v1.model.entity.BudgetItems;
import dev.gunho.api.bingous.v1.repository.BudgetItemsRepository;
import dev.gunho.api.bingous.v1.repository.UserRepository;
import dev.gunho.api.global.constants.CoreConstants;
import dev.gunho.api.global.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetItemsService {

    private final UserRepository userRepository;
    private final BudgetItemsRepository budgetItemsRepository;

    public Mono<BudgetItems> create(BudgetItemsDto.Request request, ServerHttpRequest httpRequest) {
        String key = httpRequest.getHeaders().getFirst(CoreConstants.Network.AUTH_KEY);

        if (Util.CommonUtil.isEmpty(key)) {
            log.error("가계부 등록 중 오류가 발생했습니다");
            return Mono.error(new RuntimeException("AUTH_KEY가 없습니다."));
        }

        return userRepository.findBySessionKey(key)
                .flatMap(user -> {
                    LocalDateTime now = LocalDateTime.now();

                    // 삽입 후 해당 엔티티 반환
                    return budgetItemsRepository.insertBudgetItem(
                            user.getCoupleId(),
                            request.paidBy(),
                            request.title(),
                            request.description(),
                            request.location(),
                            request.amount(),
                            request.category(),
                            request.date(),
                            user.getId(),
                            now,
                            now
                    ).then(budgetItemsRepository.findLastInsertedBudgetItem());
                });

    }

    public Flux<BudgetItems> searchMonth(ServerRequest request) {
        String key = request.exchange().getRequest().getHeaders().getFirst(CoreConstants.Network.AUTH_KEY);
        if (Util.CommonUtil.isEmpty(key)) {
            log.error("가계부 검색 중 인증 오류가 발생했습니다");
            return Flux.error(new RuntimeException("인증 오류가 발생했습니다"));
        }

        String month = request.queryParam("month").orElse(null);
        // 값이 NULL이거나 유효하지 않은 경우 처리 (현재 월 기본값으로 설정)
        if (Util.CommonUtil.isEmpty(month) || !month.matches("\\d{4}-\\d{2}")) { // "yyyy-MM" 형식 검증
            log.warn("month 파라미터가 잘못되었거나 누락됨. 기본값으로 처리합니다.");
            month = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")); // 현재 월 사용
        }

        // yyyy-MM -> yyyyMM으로 변환
        String formattedMonth = month.replaceAll("-", ""); // "2025-08" -> "202508" 변환



        return userRepository.findBySessionKey(key)
                .flatMapMany(user -> {
                    return budgetItemsRepository.findAllByMonth(formattedMonth, user.getCoupleId());
                })
                .switchIfEmpty(Flux.empty());
    }
}
