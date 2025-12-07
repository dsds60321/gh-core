package dev.gunho.api.stock.v1.repository;

import dev.gunho.api.stock.v1.entity.StockMetaEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface StockMetaRepository extends ReactiveCrudRepository<StockMetaEntity, Long> {
    Flux<StockMetaEntity> findByActiveYn(String activeYn);

    @Query("SELECT * FROM stock.stock_meta where active_yn = 'Y'")
    Flux<StockMetaEntity> findByActive();
}
