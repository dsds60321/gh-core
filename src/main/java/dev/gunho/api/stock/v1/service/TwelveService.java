package dev.gunho.api.stock.v1.service;

import dev.gunho.api.global.util.Util;
import dev.gunho.api.stock.v1.client.TwelveClient;
import dev.gunho.api.stock.v1.entity.StockDailyEntity;
import dev.gunho.api.stock.v1.entity.StockMetaEntity;
import dev.gunho.api.stock.v1.model.TwelveDataTimeSeriesResponse;
import dev.gunho.api.stock.v1.repository.StockDailyRepository;
import dev.gunho.api.stock.v1.repository.StockMetaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwelveService {

    private final StockMetaRepository stockMetaRepository;
    private final StockDailyRepository stockDailyRepository;
    private final TwelveClient twelveClient;

    public Mono<Void> saveDailyStock() {
        return stockMetaRepository.findByActiveYn("Y")
                .doOnSubscribe(subscription -> log.info("[saveDailyStock] stock_meta 처리 시작"))
                .concatMap(stockMeta -> twelveClient.fetchDaily(stockMeta.getSymbol())
                        .flatMapMany(response -> {
                            TwelveDataTimeSeriesResponse.Meta meta = response.meta();

                            log.info("response meta : {} " , response.toString());
                            if (Util.CommonUtil.isEmpty(meta)) {
                                log.warn("[notiDailyStock] TwelveData 응답에 meta 없음 - symbol={}", stockMeta.getSymbol());
                                return Flux.<StockDailyEntity>empty();
                            }

                            return Flux.fromIterable(response.valuesOrEmpty())
                                    .map(value -> response.toEntity(meta, value))
                                    .flatMap(stockDailyRepository::save)
                                    .doOnError(ex -> log.error("[saveDailyStock] ERROR : {} | {} ", stockMeta.getSymbol(), ex.getMessage()));

                        })
                        .onErrorResume(ex -> {
                            log.error("[notiDailyStock] 일별 데이터 수집 실패 - symbol={}", stockMeta.getSymbol(), ex);
                            return Flux.empty();
                        }))
                .then();
    }
}
