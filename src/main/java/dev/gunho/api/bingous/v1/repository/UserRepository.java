package dev.gunho.api.bingous.v1.repository;

import dev.gunho.api.bingous.v1.model.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, String> {

    Mono<Boolean> existsById(String id);
    Mono<Boolean> existsByEmail(String email);


    @Modifying
    @Query("UPDATE users SET tryCnt = tryCnt + 1, updated_at = NOW() WHERE id = :id")
    Mono<Integer> incrementTryCnt(@Param("id") String id);

    @Modifying
    @Query("UPDATE users SET tryCnt = 0, last_login_at = NOW(), updated_at = NOW() WHERE id = :id")
    Mono<Integer> updateLastLogin(@Param("id") String id);



}
