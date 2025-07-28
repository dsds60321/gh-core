package dev.gunho.api.bingous.v1.repository;

import dev.gunho.api.bingous.v1.model.entity.Schedules;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ScheduleRepository  extends ReactiveCrudRepository<Schedules, Long> {
    Flux<Schedules> findAllByCoupleId(Long coupleId);
}
