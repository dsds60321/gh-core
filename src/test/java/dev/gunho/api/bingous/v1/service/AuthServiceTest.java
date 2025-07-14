package dev.gunho.api.bingous.v1.service;

import dev.gunho.api.bingous.v1.model.dto.EmailVerify;
import dev.gunho.api.bingous.v1.model.dto.SignUp;
import dev.gunho.api.global.model.Result;
import dev.gunho.api.global.model.dto.EmailResponse;
import dev.gunho.api.global.service.EmailService;
import dev.gunho.api.global.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 테스트")
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private ServerHttpRequest serverHttpRequest;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        // Mock 객체 초기화
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signUpSuccess() {
        // Given - record 생성자 사용
        SignUp.Request request = new SignUp.Request(
                "testuser",
                "test@example.com",
                "testnickname",
                "password123",
                "010-1234-5678",
                null,
                null,
                false
        );

        SignUp.Response response = SignUp.Response.builder()
                .message("회원가입이 완료되었습니다.")
                .userId("testuser")
                .sessionKey("test-session-key")
                .success(true)
                .build();

        Result<SignUp.Response> expectedResult = Result.success(response);

        when(userService.signUp(any(SignUp.Request.class), any(ServerHttpRequest.class)))
                .thenReturn(Mono.just(expectedResult));

        // When
        Mono<Result<SignUp.Response>> result = authService.signUp(request, serverHttpRequest);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(r -> r.isSuccess() && r.getData().getUserId().equals("testuser"))
                .verifyComplete();
    }

    @Test
    @DisplayName("이메일 인증 코드 전송 성공 테스트")
    void verifyEmailSuccess() {
        // Given - record 생성자 사용
        EmailVerify.Request request = new EmailVerify.Request(
                "testuser",
                "test@example.com"
        );

        EmailResponse expectedResponse = EmailResponse.builder()
                .success(true)
                .message("인증 코드가 전송되었습니다.")
                .build();

        when(redisUtil.setString(anyString(), anyString(), any(Duration.class)))
                .thenReturn(Mono.just(true));
        when(emailService.sendTemplateEmail(any(), anyList(), anyString()))
                .thenReturn(Mono.just(expectedResponse));

        // When
        Mono<EmailResponse> result = authService.verifyEmail(request);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.isSuccess() &&
                        response.getMessage().equals("인증 코드가 전송되었습니다."))
                .verifyComplete();
    }

    @Test
    @DisplayName("이메일 인증 코드 확인 성공 테스트")
    void confirmEmailSuccess() {
        // Given - record 생성자 사용
        EmailVerify.VerifyCodeRequest request = new EmailVerify.VerifyCodeRequest(
                "test@example.com",
                "123456"
        );

        when(redisUtil.getString(anyString()))
                .thenReturn(Mono.just("123456"));

        // When
        Mono<EmailVerify.VerifyCodeResponse> result = authService.confirmEmail(request);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.verified() &&
                        response.message().equals("인증에 성공했습니다."))
                .verifyComplete();
    }

    @Test
    @DisplayName("이메일 인증 코드 확인 실패 테스트")
    void confirmEmailFailure() {
        // Given - record 생성자 사용
        EmailVerify.VerifyCodeRequest request = new EmailVerify.VerifyCodeRequest(
                "test@example.com",
                "123456"
        );

        when(redisUtil.getString(anyString()))
                .thenReturn(Mono.just("654321")); // 다른 코드

        // When
        Mono<EmailVerify.VerifyCodeResponse> result = authService.confirmEmail(request);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> !response.verified() &&
                        response.message().equals("인증에 실패했습니다."))
                .verifyComplete();
    }

    @Test
    @DisplayName("Redis 저장 실패 테스트")
    void verifyEmailRedisFailure() {
        // Given - record 생성자 사용
        EmailVerify.Request request = new EmailVerify.Request(
                "testuser",
                "test@example.com"
        );

        when(redisUtil.setString(anyString(), anyString(), any(Duration.class)))
                .thenReturn(Mono.just(false));

        // When
        Mono<EmailResponse> result = authService.verifyEmail(request);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> !response.isSuccess() &&
                        response.getMessage().equals("인증 코드 저장에 실패했습니다. 다시 시도해주세요."))
                .verifyComplete();
    }
}
