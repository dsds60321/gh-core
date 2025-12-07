package dev.gunho.api.stock.v1.client;

import dev.gunho.api.stock.v1.config.TwelveProperties;
import dev.gunho.api.stock.v1.enums.TwelveDataEnums;
import dev.gunho.api.stock.v1.model.TwelveDataTimeSeriesRequest;
import dev.gunho.api.stock.v1.model.TwelveDataTimeSeriesResponse;
import dev.gunho.api.stock.v1.service.TwelveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

import static dev.gunho.api.stock.v1.enums.TwelveDataEnums.*;
import static dev.gunho.api.stock.v1.enums.TwelveDataEnums.PATH.TIME_SERIES_DAILY;

@Slf4j
@Component
@RequiredArgsConstructor
public class TwelveClient {

    private final WebClient twelveWebClient;
    private final TwelveProperties properties;

    public Mono<TwelveDataTimeSeriesResponse> fetchDaily(String symbol) {

        return twelveWebClient.get()
                .uri(uriBuilder -> uriBuilder.path(TIME_SERIES_DAILY.getValue())
                        .queryParam("symbol", symbol)
                        .queryParam("interval", INTERVAL.ONE_DAY.getValue())
                        .queryParam("outputsize", 5000)
                        .queryParam("order", "asc")
                        .queryParam("start_date", LocalDate.now().minusDays(3))
                        .queryParam("apikey", properties.getApiKey())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(String.class)
                        .defaultIfEmpty("unkonwo error")
                        .flatMap(body -> Mono.error(new IllegalStateException("TwelveData 호출 실패 " + clientResponse.statusCode() + " - " + body))))
                .bodyToMono(TwelveDataTimeSeriesResponse.class)
                .flatMap(this::validateResponse)
                .retryWhen(Retry.backoff(properties.getMaxRetry(), properties.getRetryBackoff())
                        .filter(this::isRetryable)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure()))
                .timeout(properties.getReadTimeout().plusSeconds(5))
                .doOnSubscribe(s -> log.debug("TwelveData time_series 호출 - symbol={}", symbol));
    }

    /**
     * 등록된 주가 가져오기
     * @param request
     * @return
     */
    public Mono<TwelveDataTimeSeriesResponse> fetchDailySeries(TwelveDataTimeSeriesRequest request) {
        log.info("fetchDailySeries - symbol: {}", request.symbol());

        return twelveWebClient.get()
                .uri(uriBuilder -> uriBuilder.path(TIME_SERIES_DAILY.getValue())
                        .queryParam("symbol", request.symbol())
                        .queryParam("interval", request.interval().getValue())
                        .queryParam("outputsize", 5000)
                        .queryParam("order", "asc")
                        .queryParam("start_date", request.startDate().toString())
                        .queryParam("end_date", request.endDate().toString())
                        .queryParam("apikey", properties.getApiKey())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(String.class)
                        .defaultIfEmpty("unkonwo error")
                        .flatMap(body -> Mono.error(new IllegalStateException("TwelveData 호출 실패 " + clientResponse.statusCode() + " - " + body))))
                .bodyToMono(TwelveDataTimeSeriesResponse.class)
                .flatMap(this::validateResponse)
                .retryWhen(Retry.backoff(properties.getMaxRetry(), properties.getRetryBackoff())
                        .filter(this::isRetryable)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure()))
                .timeout(properties.getReadTimeout().plusSeconds(5))
                .doOnSubscribe(s -> log.debug("TwelveData time_series 호출 - symbol={}, start={}, end={}", request.symbol(), request.startDate(), request.endDate()));
    }

    private Mono<TwelveDataTimeSeriesResponse> validateResponse(TwelveDataTimeSeriesResponse response) {
        if (response.hasError()) {
            String code = response.code() == null ? "N/A" : response.code();
            String message = response.message() == null ? "unknown" : response.message();
            return Mono.error(new IllegalStateException("TwelveData 오류 - code=" + code + ", message=" + message));
        }
        return Mono.just(response);
    }

    private boolean isRetryable(Throwable throwable) {
        return throwable instanceof TimeoutException
                || throwable instanceof WebClientRequestException
                || (throwable instanceof WebClientResponseException responseException
                && responseException.getStatusCode().is5xxServerError());
    }
}
