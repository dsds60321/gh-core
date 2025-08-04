package dev.gunho.api.bingous.v1.repository;

import dev.gunho.api.bingous.v1.model.entity.Reflection;
import org.reactivestreams.Publisher;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ReflectionRepository extends ReactiveCrudRepository<Reflection, Long> {
    Flux<Reflection> findAllByCoupleId(Long coupleId);
}
