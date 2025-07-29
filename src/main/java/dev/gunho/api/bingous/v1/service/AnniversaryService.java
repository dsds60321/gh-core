package dev.gunho.api.bingous.v1.service;

import dev.gunho.api.bingous.v1.model.dto.AnniversaryDto;
import dev.gunho.api.bingous.v1.model.entity.Anniversary;
import dev.gunho.api.bingous.v1.repository.AnniversaryRepository;
import dev.gunho.api.bingous.v1.repository.UserRepository;
import dev.gunho.api.global.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static dev.gunho.api.global.constants.CoreConstants.Network;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnniversaryService {

    private final UserRepository userRepository;
    private final AnniversaryRepository anniversaryRepository;

    public Mono<Boolean> create(AnniversaryDto.Request request, ServerHttpRequest httpRequest) {
        String key = httpRequest.getHeaders().getFirst(Network.AUTH_KEY);

        if (Util.CommonUtil.isEmpty(key)) {
            log.error("기념일 등록 중 오류가 발생했습니다");
            return Mono.just(false);
        }

        return userRepository.findBySessionKey(key)
                .flatMap(user -> {

                    Anniversary anniversary = request.toEntity().toBuilder()
                            .createdBy(user.getId())
                            .coupleId(user.getCoupleId())
                            .build();

                    return anniversaryRepository.save(anniversary)
                            .map(anniversarySaved -> true);

                })
                .onErrorResume(error -> {
                    log.error("기념일 등록 중 오류가 발생했습니다: {}", error.getMessage());
                    return Mono.just(false);
                });

    }

    public Flux<Anniversary> searchAnniversaries(ServerRequest request) {
        String key = request.exchange().getRequest().getHeaders().getFirst(Network.AUTH_KEY);
        if (Util.CommonUtil.isEmpty(key)) {
            log.error("기념일 검색 중 인증 오류가 발생했습니다");
            return Flux.error(new RuntimeException("인증 오류가 발생했습니다"));
        }


        // 날짜 파라미터 추출 (startDate, endDate 형식으로 검색)a
        LocalDate startDate = request.queryParam("startDate")
                .map(Util.Date::parseDate)
                .orElse(LocalDate.now().minusMonths(1)); // 기본값: 1개월 전

        LocalDate endDate = request.queryParam("endDate")
                .map(Util.Date::parseDate)
                .orElse(LocalDate.now().plusMonths(1)); // 기본값: 1개월 후

        return userRepository.findBySessionKey(key)
                .flatMapMany(user -> {
                    return anniversaryRepository.findAllByDateBetweenAndCoupleId(startDate, endDate, user.getCoupleId());
                })
                .switchIfEmpty(Flux.empty());
    }



}
