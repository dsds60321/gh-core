package dev.gunho.api.ongimemo.v1.service;

import dev.gunho.api.global.constants.CoreConstants;
import dev.gunho.api.global.exception.CustomException;
import dev.gunho.api.global.util.Util;
import dev.gunho.api.ongimemo.v1.model.dto.PraiseDto;
import dev.gunho.api.ongimemo.v1.model.dto.ReflectionDTO;
import dev.gunho.api.ongimemo.v1.repository.OngiMemoReflectionRepository;
import dev.gunho.api.ongimemo.v1.repository.PraiseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Slf4j
@Service
@RequiredArgsConstructor
public class PraiseService {

    private final PraiseRepository praiseRepository;


    public Flux<PraiseDto.WithRecipientsResponse> search(ServerRequest request) {
        String key = request.exchange().getRequest().getHeaders().getFirst(CoreConstants.Network.AUTH_KEY);
        if (Util.CommonUtil.isEmpty(key)) {
            throw new CustomException("인증받지 않은 아이디 입니다. 로그인 후 다시 이용해주세요.");
        }

        // startDate 처리
        LocalDateTime startDate = request.queryParam("start_date")
                        .map(Util.Date::parseDateTime)
                                .orElse(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay());

        LocalDateTime endDate = request.queryParam("end_date")
                .map(Util.Date::parseDateTime)
                .orElse(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59));

        log.info("search - Start Date: {}, End Date: {}", startDate, endDate);
        return praiseRepository.findAllByCreatedAtBetween(startDate, endDate);
    }

    public Mono<?> create(PraiseDto.Request request) {
        return null;
    }
}
