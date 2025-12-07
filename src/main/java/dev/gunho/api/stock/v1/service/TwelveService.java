package dev.gunho.api.stock.v1.service;

import dev.gunho.api.global.util.Util;
import dev.gunho.api.stock.v1.client.TwelveClient;
import dev.gunho.api.stock.v1.entity.StockDailyEntity;
import dev.gunho.api.stock.v1.model.TwelveDataTimeSeriesResponse;
import dev.gunho.api.stock.v1.repository.StockDailyRepository;
import dev.gunho.api.stock.v1.repository.StockMetaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
                                    .map(value -> {
                                        StockDailyEntity origin = response.toEntity(meta, value);
                                        log.info("origin entity class = {}", origin.getClass());
                                        return StockDailyEntity.builder()
                                                .symbol(origin.getSymbol())
                                                .tradeDate(origin.getTradeDate())
                                                .openPrice(origin.getOpenPrice())
                                                .highPrice(origin.getHighPrice())
                                                .lowPrice(origin.getLowPrice())
                                                .closePrice(origin.getClosePrice())
                                                .adjClose(origin.getAdjClose())
                                                .volume(origin.getVolume())
                                                .turnover(origin.getTurnover())
                                                .build();
                                    })
                                    .doOnNext(e -> log.info("will save entity class = {}", e.getClass()))
                                    .flatMap(stockDailyRepository::save)

                                    .doOnError(ex -> log.error("[saveDailyStock] ERROR : {} | {} ",
                                            stockMeta.getSymbol(), ex.getMessage()));


                        })
                        .onErrorResume(ex -> {
                            log.error("[notiDailyStock] 일별 데이터 수집 실패 - symbol={}", stockMeta.getSymbol(), ex);
                            return Flux.empty();
                        }))
                .then();
    }

}
