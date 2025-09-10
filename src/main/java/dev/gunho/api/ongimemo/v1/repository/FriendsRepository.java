package dev.gunho.api.ongimemo.v1.repository;

import dev.gunho.api.ongimemo.v1.model.entity.Friend;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface FriendsRepository extends ReactiveCrudRepository<Friend, Long> {

    @Query("SELECT B.* , A.status FROM friends A LEFT OUTER JOIN ongimemo.users B on A.friend_idx = B.idx WHERE A.user_idx = :userIdx")
    Flux<Friend> findAllWithUserByUserIdx(Long userIdx);

}
