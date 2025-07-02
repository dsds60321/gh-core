package dev.gunho.api.bingous.v1.model.dto;

import jakarta.validation.constraints.NotBlank;

public class SignUp {

    public record Request(
            String email,
            String username,
            String password,
            String fullName,
            String phoneNumber,
            Boolean marketingAgreed
    ) {}


    public static class Response {

    }
}
