package dev.gunho.api.bingous.v1.service;


import dev.gunho.api.bingous.v1.model.dto.ReflectionDto;
import dev.gunho.api.bingous.v1.model.entity.Reflection;
import dev.gunho.api.bingous.v1.repository.ReflectionRepository;
import dev.gunho.api.bingous.v1.repository.UserRepository;
import dev.gunho.api.global.constants.CoreConstants;
import dev.gunho.api.global.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class ReflectionService {


    private final UserRepository userRepository;
    private final ReflectionRepository reflectionRepository;

    public Mono<Reflection> create(ReflectionDto.Request request, ServerHttpRequest httpRequest) {
        String key = httpRequest.getHeaders().getFirst(CoreConstants.Network.AUTH_KEY);

        if (Util.CommonUtil.isEmpty(key)) {
            log.error("반성문 등록 중 인증 키가 없습니다");
            return Mono.empty();
        }

        return userRepository.findBySessionKey(key)
                .flatMap(user -> {
                    // 사용자 정보로 반성문 엔티티 생성 (요청의 authorUserId 대신 인증된 사용자 ID 사용)
                    Reflection reflectionEntity = Reflection.builder()
                            .coupleId(user.getCoupleId()) // 인증된 사용자의 커플 ID 사용
                            .authorUserId(user.getId()) // 인증된 사용자 ID 사용
                            .approverUserId(request.approverUserId())
                            .incident(request.incident())
                            .reason(request.reason())
                            .improvement(request.improvement())
                            .status("PENDING")
                            .createdAt(LocalDateTime.now())
                            .build();

                    return reflectionRepository.save(reflectionEntity);
                })
                .doOnSuccess(reflection -> {
                    if (reflection != null) {
                        log.info("반성문이 성공적으로 등록되었습니다. reflectionId: {}, authorUserId: {}",
                                reflection.getId(), reflection.getAuthorUserId());
                    }
                })
                .onErrorResume(error -> {
                    log.error("반성문 등록 중 오류가 발생했습니다: {}", error.getMessage(), error);
                    return Mono.empty(); // 또는 Mono.error(error)로 에러를 전파
                });
    }


    /**
     * 반성문 상태 업데이트 (승인/반려)
     */
    public Mono<Boolean> updateStatus(Long reflectionId, ReflectionDto.StatusUpdate statusUpdate, ServerHttpRequest httpRequest) {
        String key = httpRequest.getHeaders().getFirst(CoreConstants.Network.AUTH_KEY);

        if (Util.CommonUtil.isEmpty(key)) {
            log.error("반성문 상태 업데이트 중 인증 오류가 발생했습니다");
            return Mono.just(false);
        }

        return userRepository.findBySessionKey(key)
                .flatMap(approver ->
                        reflectionRepository.findById(reflectionId)
                                .flatMap(reflection -> {
                                    // 권한 체크: 같은 커플이고, 작성자가 아닌 경우만 승인/반려 가능
                                    if (!reflection.getCoupleId().equals(approver.getCoupleId())) {
                                        log.error("다른 커플의 반성문에 접근할 수 없습니다. reflectionId: {}, approverCoupleId: {}",
                                                reflectionId, approver.getCoupleId());
                                        return Mono.just(false);
                                    }

                                    if (reflection.getAuthorUserId().equals(approver.getId())) {
                                        log.error("본인이 작성한 반성문은 승인/반려할 수 없습니다. reflectionId: {}, userId: {}",
                                                reflectionId, approver.getId());
                                        return Mono.just(false);
                                    }

                                    // 이미 처리된 반성문인지 체크
                                    if (!reflection.isPending()) {
                                        log.error("이미 처리된 반성문입니다. reflectionId: {}, status: {}",
                                                reflectionId, reflection.getStatus());
                                        return Mono.just(false);
                                    }

                                    // 상태 업데이트
                                    Reflection updatedReflection;
                                    if ("APPROVED".equals(statusUpdate.status())) {
                                        updatedReflection = reflection.approve(approver.getId());
                                    } else { // REJECTED
                                        updatedReflection = reflection.reject(approver.getId(), statusUpdate.feedback());
                                    }

                                    return reflectionRepository.save(updatedReflection)
                                            .map(saved -> true);
                                })
                                .switchIfEmpty(Mono.fromSupplier(() -> {
                                    log.error("존재하지 않는 반성문입니다. reflectionId: {}", reflectionId);
                                    return false;
                                }))
                )
                .onErrorResume(error -> {
                    log.error("반성문 상태 업데이트 중 오류가 발생했습니다: {}", error.getMessage());
                    return Mono.just(false);
                });
    }

    public Mono<Reflection> detail(ServerRequest request) {
        String key = request.exchange().getRequest().getHeaders().getFirst(CoreConstants.Network.AUTH_KEY);
        Long reflectionId = Long.valueOf(request.pathVariable("reflectionId"));

        if (Util.CommonUtil.isEmpty(key)) {
            log.error("반성문 검색 중 인증 오류가 발생했습니다");
            return Mono.error(new RuntimeException("인증 오류가 발생했습니다"));
        }

        return userRepository.findBySessionKey(key)
                .flatMap(user -> {
                    return reflectionRepository.findById(reflectionId);
                })
                .switchIfEmpty(Mono.empty());
    }

    public Flux<Reflection> search(ServerRequest request) {
        String key = request.exchange().getRequest().getHeaders().getFirst(CoreConstants.Network.AUTH_KEY);
        if (Util.CommonUtil.isEmpty(key)) {
            log.error("반성문 검색 중 인증 오류가 발생했습니다");
            return Flux.error(new RuntimeException("인증 오류가 발생했습니다"));
        }

        return userRepository.findBySessionKey(key)
                .flatMapMany(user -> {
                    return reflectionRepository.findAllByCoupleId(user.getCoupleId());
                })
                .switchIfEmpty(Flux.empty());
    }
}
