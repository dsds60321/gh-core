package dev.gunho.api.global.util;

import dev.gunho.api.global.model.dto.Response;
import dev.gunho.api.global.enums.ResponseCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * 응답 유틸
 */
public class ResponseUtil {

    // ========== 성공 응답 ==========

    /**
     * 성공 응답 (기본)
     */
    public static <T> Mono<ServerResponse> ok(T data) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Response.success(data));
    }

    /**
     * 성공 응답 (커스텀 설명)
     */
    public static <T> Mono<ServerResponse> ok(T data, String description) {
        Response<T> response = new Response<>(ResponseCode.SUCCESS, description, data);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response);
    }

    // ========== 클라이언트 오류 응답 (400번대) ==========

    /**
     * 잘못된 요청 응답 (400)
     */
    public static <T> Mono<ServerResponse> badRequest(T data) {
        return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Response.fail(data));
    }

    /**
     * 잘못된 요청 응답 (커스텀 설명)
     */
    public static <T> Mono<ServerResponse> badRequest(T data, String description) {
        Response<T> response = new Response<>(ResponseCode.BAD_REQUEST, description, data);
        return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response);
    }

    /**
     * 검증 오류 응답 (400) - ValidationException 전용
     */
    public static <T> Mono<ServerResponse> validationError(T errorData, String description) {
        Response<T> response = new Response<>(ResponseCode.BAD_REQUEST, description, errorData);
        return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response);
    }

    // ========== 서버 오류 응답 (500번대) ==========

    /**
     * 서버 내부 오류 응답 (500)
     */
    public static <T> Mono<ServerResponse> internalServerError(T data) {
        return ServerResponse.status(500)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Response.error(data));
    }

    /**
     * 서버 내부 오류 응답 (커스텀 설명)
     */
    public static <T> Mono<ServerResponse> internalServerError(T data, String description) {
        Response<T> response = new Response<>(ResponseCode.INTERNAL_SERVER_ERROR, description, data);
        return ServerResponse.status(500)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response);
    }

    // ========== 범용 응답 메서드 ==========

    /**
     * ResponseCode를 직접 지정하는 범용 메서드
     */
    public static <T> Mono<ServerResponse> response(ResponseCode responseCode, T data) {
        Response<T> response = new Response<>(responseCode, data);
        return ServerResponse.status(getHttpStatus(responseCode))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response);
    }

    /**
     * ResponseCode와 커스텀 설명을 지정하는 범용 메서드
     */
    public static <T> Mono<ServerResponse> response(ResponseCode responseCode, T data, String description) {
        Response<T> response = new Response<>(responseCode, description, data);
        return ServerResponse.status(getHttpStatus(responseCode))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response);
    }

    // ========== 기존 메서드 (하위 호환성) ==========

    /**
     * @deprecated badRequest() 사용 권장
     */
    @Deprecated
    public static <T> Mono<ServerResponse> fail(T data) {
        return badRequest(data);
    }

    // ========== 유틸리티 메서드 ==========

    /**
     * ResponseCode에 따른 HTTP 상태 코드 매핑
     */
    private static int getHttpStatus(ResponseCode responseCode) {
        return switch (responseCode) {
            case SUCCESS -> 200;
            case BAD_REQUEST -> 400;
            // 추후 ResponseCode 추가 시 여기에 매핑
            case INTERNAL_SERVER_ERROR -> 500;
            default -> 200;
        };
    }
}
