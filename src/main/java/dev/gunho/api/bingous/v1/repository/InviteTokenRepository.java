package dev.gunho.api.bingous.v1.repository;

import dev.gunho.api.bingous.v1.model.entity.InviteToken;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface InviteTokenRepository extends ReactiveCrudRepository<InviteToken, Long> {


    @Modifying
    @Query("UPDATE invite_tokens SET invitee_id = :userId, couple_name = CONCAT(inviter_id, '_', :userId), status = 'accepted', updated_at = NOW() WHERE token = :token")
    Mono<Integer> updateInviteeUser(String token, String userId);
}
