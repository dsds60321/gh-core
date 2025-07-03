package dev.gunho.api.bingous.v1.repository;

import dev.gunho.api.bingous.v1.model.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, String> {

    Mono<Boolean> existsById(String id);
    Mono<Boolean> existsByEmail(String email);
    Mono<User> findByEmail(String email);


}
