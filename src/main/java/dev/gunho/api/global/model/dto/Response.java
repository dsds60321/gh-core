package dev.gunho.api.global.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.gunho.api.global.enums.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {

    private String code;
    private String message;
    private String description;
    private String timestamp;
    private T data;


    public Response(ResponseCode responseCode, T data) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
        this.description = responseCode.getDescription();
        this.timestamp = getCurrentTimestamp();
        this.data = data;
    }

    public Response(ResponseCode responseCode, String description, T data) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
        this.description = description;
        this.timestamp = getCurrentTimestamp();
        this.data = data;
    }

    public Response(ResponseCode responseCode, String customMessage, String description, T data) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
        this.description = description;
        this.timestamp = getCurrentTimestamp();
        this.data = data;
    }

    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(ResponseCode.SUCCESS, data);
    }

    public static <T> Response<T> fail(T data) {
        return new Response<>(ResponseCode.BAD_REQUEST, data);
    }

    public static <T> Response<T> error(T data) {
        return new Response<>(ResponseCode.INTERNAL_SERVER_ERROR, data);
    }



}
