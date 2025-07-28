package dev.gunho.api.bingous.v1.repository;

import dev.gunho.api.bingous.v1.model.entity.Anniversary;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface AnniversaryRepository extends ReactiveCrudRepository<Anniversary, Long> {

    Flux<Anniversary> findAllByDateBetweenAndCoupleId(LocalDate startDate, LocalDate endDate, Long coupleId);

    Flux<Anniversary> findAllByCoupleId(Long coupleId);
}
