package dev.gunho.api.ongimemo.v1.repository;

import dev.gunho.api.ongimemo.v1.model.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, String> {

    Mono<Boolean> existsById(String id);

}
