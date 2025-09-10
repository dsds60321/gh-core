package dev.gunho.api.ongimemo.v1.service;

import dev.gunho.api.global.constants.CoreConstants;
import dev.gunho.api.global.exception.CustomException;
import dev.gunho.api.global.util.Util;
import dev.gunho.api.ongimemo.v1.model.dto.FriendsDTO;
import dev.gunho.api.ongimemo.v1.repository.FriendsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FriendsService {

    private final FriendsRepository friendsRepository;

    public Mono<FriendsDTO.Response> getFriends(ServerRequest request) {
        String key = request.exchange().getRequest().getHeaders().getFirst(CoreConstants.Network.AUTH_KEY);
        if (Util.CommonUtil.isEmpty(key)) {
            throw new CustomException("인증받지 않은 아이디 입니다. 로그인 후 다시 이용해주세요.");
        }


        return null;
    }
}
