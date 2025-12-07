package dev.gunho.api.stock.v1.repository;

import dev.gunho.api.stock.v1.entity.StockDailyEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface StockDailyRepository extends ReactiveCrudRepository<StockDailyEntity, Long> {
}
