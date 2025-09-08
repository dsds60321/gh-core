package dev.gunho.api.ongimemo.v1.repository;

import dev.gunho.api.ongimemo.v1.model.dto.PraiseDto;
import dev.gunho.api.ongimemo.v1.model.entity.Praise;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@Repository
public interface PraiseRepository extends ReactiveCrudRepository<Praise, Long> {

    @Query("""
            SELECT A.idx,
                   A.user_idx,
                   A.title,
                   A.content,
                   A.status,
                   A.created_at,
                   A.updated_at,
                   GROUP_CONCAT(C.nickname SEPARATOR ', ') AS recipients
            FROM ongimemo.praises A
                     LEFT OUTER JOIN ongimemo.praise_recipients B ON A.idx = B.praise_idx
                     LEFT OUTER JOIN ongimemo.users C ON B.recipient_idx = C.idx
            WHERE A.created_at between :startDate AND :endDate
            GROUP BY A.idx
            """)
    Flux<PraiseDto.WithRecipientsResponse> findAllByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
