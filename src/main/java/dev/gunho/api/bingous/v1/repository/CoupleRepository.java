package dev.gunho.api.bingous.v1.repository;

import dev.gunho.api.bingous.v1.model.entity.Couples;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CoupleRepository extends ReactiveCrudRepository<Couples, Long> {
}
