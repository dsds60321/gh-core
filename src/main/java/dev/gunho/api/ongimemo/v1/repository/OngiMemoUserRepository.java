package dev.gunho.api.ongimemo.v1.repository;

import dev.gunho.api.ongimemo.v1.model.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface OngiMemoUserRepository extends ReactiveCrudRepository<User, Long> {

    Mono<Boolean> existsById(Long idx);

    Mono<Boolean> existsByid(String id);

    Mono<Boolean> existsByEmail(String email);
}
