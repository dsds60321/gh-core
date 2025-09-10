package dev.gunho.api.ongimemo.v1.repository;

import dev.gunho.api.ongimemo.v1.model.entity.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface OngiMemoUserRepository extends ReactiveCrudRepository<User, Long> {

    Mono<Boolean> existsById(Long idx);

    Mono<Boolean> existsByid(String id);

    Mono<Boolean> existsByEmail(String email);

    @Query("SELECT B.* FROM common.app_sessions A LEFT OUTER JOIN ongimemo.users B ON A.user_Id = B.id WHERE A.session_key = :key")
    Mono<User> findBySessionKey(String key);
}
