package dev.gunho.api.ongimemo.v1.repository;

import dev.gunho.api.ongimemo.v1.model.dto.FriendsDTO;
import dev.gunho.api.ongimemo.v1.model.entity.Friend;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface FriendsRepository extends ReactiveCrudRepository<Friend, Long> {

    // DTO(Record) 프로젝션: SELECT alias == DTO 필드명
    @Query("""
        SELECT
            B.id        AS id,
            B.email     AS email,
            B.phone     AS phone,
            B.nickname  AS nickname,
            B.gender    AS gender,
            NULL        AS bio,
            A.status    AS status
        FROM ongimemo.friends A
        LEFT JOIN ongimemo.users B ON A.friend_idx = B.idx
        WHERE A.user_idx = :userIdx
        """)
    Flux<FriendsDTO.Response> findAllWithUserByUserIdx(Long userIdx);


}
