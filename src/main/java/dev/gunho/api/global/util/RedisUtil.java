package dev.gunho.api.global.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
    private final ReactiveRedisTemplate<String, String> reactiveStringRedisTemplate;

    /**
     * 값 저장 (만료시간 없음)
     */
    public Mono<Boolean> set(String key, Object value) {
        return reactiveRedisTemplate.opsForValue()
                .set(key, value)
                .doOnSuccess(result -> log.debug("Redis SET - Key: {}, Success: {}", key, result))
                .doOnError(error -> log.error("Redis SET Error - Key: {}, Error: {}", key, error.getMessage()));
    }

    /**
     * 값 저장 (만료시간 설정)
     */
    public Mono<Boolean> setWithExpire(String key, Object value, Duration timeout) {
        return reactiveRedisTemplate.opsForValue()
                .set(key, value, timeout)
                .doOnSuccess(result -> log.debug("Redis SET with TTL - Key: {}, TTL: {}s, Success: {}",
                        key, timeout.getSeconds(), result))
                .doOnError(error -> log.error("Redis SET with TTL Error - Key: {}, Error: {}", key, error.getMessage()));
    }

    /**
     * 문자열 값 저장 (만료시간 설정)
     */
    public Mono<Boolean> setString(String key, String value, Duration timeout) {
        return reactiveStringRedisTemplate.opsForValue()
                .set(key, value, timeout)
                .doOnSuccess(result -> log.debug("Redis SET String - Key: {}, TTL: {}s, Success: {}",
                        key, timeout.getSeconds(), result))
                .doOnError(error -> log.error("Redis SET String Error - Key: {}, Error: {}", key, error.getMessage()));
    }

    /**
     * 값 조회
     */
    public Mono<Object> get(String key) {
        return reactiveRedisTemplate.opsForValue()
                .get(key)
                .doOnSuccess(result -> log.debug("Redis GET - Key: {}, Found: {}", key, result != null))
                .doOnError(error -> log.error("Redis GET Error - Key: {}, Error: {}", key, error.getMessage()));
    }

    /**
     * 문자열 값 조회
     */
    public Mono<String> getString(String key) {
        log.info("getString : {}", key);
        return reactiveStringRedisTemplate.opsForValue()
                .get(key)
                .timeout(Duration.ofSeconds(5)) // 5초 타임아웃 설정
                .doOnSubscribe(subscription -> log.info("Redis GET String - Started for key: {}", key))
                .doOnSuccess(result -> log.info("Redis GET String - Key: {}, Result: {}", key, result))
                .doOnError(error -> log.error("Redis GET String Error - Key: {}, Error: {}", key, error.getMessage(), error))
                .doOnCancel(() -> log.warn("Redis GET String - Cancelled for key: {}", key));
    }


    /**
     * 키 삭제
     */
    public Mono<Long> delete(String key) {
        return reactiveRedisTemplate.delete(key)
                .doOnSuccess(result -> log.debug("Redis DELETE - Key: {}, Deleted count: {}", key, result))
                .doOnError(error -> log.error("Redis DELETE Error - Key: {}, Error: {}", key, error.getMessage()));
    }

    /**
     * 여러 키 삭제
     */
    public Mono<Long> delete(String... keys) {
        return reactiveRedisTemplate.delete(keys)
                .doOnSuccess(result -> log.debug("Redis DELETE Multiple - Keys: {}, Deleted count: {}", keys, result))
                .doOnError(error -> log.error("Redis DELETE Multiple Error - Keys: {}, Error: {}", keys, error.getMessage()));
    }

    /**
     * 키 존재 여부 확인
     */
    public Mono<Boolean> hasKey(String key) {
        return reactiveRedisTemplate.hasKey(key)
                .doOnSuccess(result -> log.debug("Redis EXISTS - Key: {}, Exists: {}", key, result))
                .doOnError(error -> log.error("Redis EXISTS Error - Key: {}, Error: {}", key, error.getMessage()));
    }

    /**
     * 키의 만료시간 설정
     */
    public Mono<Boolean> expire(String key, Duration timeout) {
        return reactiveRedisTemplate.expire(key, timeout)
                .doOnSuccess(result -> log.debug("Redis EXPIRE - Key: {}, TTL: {}s, Success: {}",
                        key, timeout.getSeconds(), result))
                .doOnError(error -> log.error("Redis EXPIRE Error - Key: {}, Error: {}", key, error.getMessage()));
    }

    /**
     * 키의 남은 만료시간 조회
     */
    public Mono<Duration> getExpire(String key) {
        return reactiveRedisTemplate.getExpire(key)
                .doOnSuccess(result -> log.debug("Redis TTL - Key: {}, TTL: {}s", key, result.getSeconds()))
                .doOnError(error -> log.error("Redis TTL Error - Key: {}, Error: {}", key, error.getMessage()));
    }

    /**
     * 패턴으로 키 검색
     */
    public Flux<String> keys(String pattern) {
        return reactiveRedisTemplate.keys(pattern)
                .doOnComplete(() -> log.debug("Redis KEYS - Pattern: {}, Search completed", pattern))
                .doOnError(error -> log.error("Redis KEYS Error - Pattern: {}, Error: {}", pattern, error.getMessage()));
    }

    /**
     * Hash 저장
     */
    public Mono<Boolean> hSet(String key, String hashKey, Object value) {
        return reactiveRedisTemplate.opsForHash()
                .put(key, hashKey, value)
                .doOnSuccess(result -> log.debug("Redis HSET - Key: {}, HashKey: {}, Success: {}", key, hashKey, result))
                .doOnError(error -> log.error("Redis HSET Error - Key: {}, HashKey: {}, Error: {}", key, hashKey, error.getMessage()));
    }

    /**
     * Hash 조회
     */
    public Mono<Object> hGet(String key, String hashKey) {
        return reactiveRedisTemplate.opsForHash()
                .get(key, hashKey)
                .doOnSuccess(result -> log.debug("Redis HGET - Key: {}, HashKey: {}, Found: {}", key, hashKey, result != null))
                .doOnError(error -> log.error("Redis HGET Error - Key: {}, HashKey: {}, Error: {}", key, hashKey, error.getMessage()));
    }

    /**
     * Hash 삭제
     */
    public Mono<Long> hDelete(String key, String... hashKeys) {
        return reactiveRedisTemplate.opsForHash()
                .remove(key, (Object[]) hashKeys)
                .doOnSuccess(result -> log.debug("Redis HDEL - Key: {}, HashKeys: {}, Deleted count: {}", key, hashKeys, result))
                .doOnError(error -> log.error("Redis HDEL Error - Key: {}, HashKeys: {}, Error: {}", key, hashKeys, error.getMessage()));
    }

    /**
     * Set에 값 추가
     */
    public Mono<Long> sAdd(String key, Object... values) {
        return reactiveRedisTemplate.opsForSet()
                .add(key, values)
                .doOnSuccess(result -> log.debug("Redis SADD - Key: {}, Added count: {}", key, result))
                .doOnError(error -> log.error("Redis SADD Error - Key: {}, Error: {}", key, error.getMessage()));
    }

    /**
     * Set에서 값 제거
     */
    public Mono<Long> sRemove(String key, Object... values) {
        return reactiveRedisTemplate.opsForSet()
                .remove(key, values)
                .doOnSuccess(result -> log.debug("Redis SREM - Key: {}, Removed count: {}", key, result))
                .doOnError(error -> log.error("Redis SREM Error - Key: {}, Error: {}", key, error.getMessage()));
    }

    /**
     * Set의 모든 멤버 조회
     */
    public Flux<Object> sMembers(String key) {
        return reactiveRedisTemplate.opsForSet()
                .members(key)
                .doOnComplete(() -> log.debug("Redis SMEMBERS - Key: {}, Query completed", key))
                .doOnError(error -> log.error("Redis SMEMBERS Error - Key: {}, Error: {}", key, error.getMessage()));
    }

    /**
     * 카운터 증가
     */
    public Mono<Long> increment(String key) {
        return reactiveRedisTemplate.opsForValue()
                .increment(key)
                .doOnSuccess(result -> log.debug("Redis INCR - Key: {}, New value: {}", key, result))
                .doOnError(error -> log.error("Redis INCR Error - Key: {}, Error: {}", key, error.getMessage()));
    }

    /**
     * 카운터 증가 (지정된 값만큼)
     */
    public Mono<Long> increment(String key, long delta) {
        return reactiveRedisTemplate.opsForValue()
                .increment(key, delta)
                .doOnSuccess(result -> log.debug("Redis INCRBY - Key: {}, Delta: {}, New value: {}", key, delta, result))
                .doOnError(error -> log.error("Redis INCRBY Error - Key: {}, Error: {}", key, error.getMessage()));
    }

    /**
     * 카운터 감소
     */
    public Mono<Long> decrement(String key) {
        return reactiveRedisTemplate.opsForValue()
                .decrement(key)
                .doOnSuccess(result -> log.debug("Redis DECR - Key: {}, New value: {}", key, result))
                .doOnError(error -> log.error("Redis DECR Error - Key: {}, Error: {}", key, error.getMessage()));
    }
}
