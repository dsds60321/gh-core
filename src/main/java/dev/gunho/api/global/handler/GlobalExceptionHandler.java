package dev.gunho.api.global.handler;

import dev.gunho.api.global.exception.CustomException;
import dev.gunho.api.global.model.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustom(CustomException ex) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.failure(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAny(Exception ex) {
        return ResponseEntity.internalServerError()
                .body(ApiResponse.systemError("서버 오류가 발생했습니다."));
    }

}
