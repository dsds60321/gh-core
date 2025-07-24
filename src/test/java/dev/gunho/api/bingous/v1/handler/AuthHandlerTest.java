package dev.gunho.api.bingous.v1.handler;

import dev.gunho.api.bingous.v1.model.dto.EmailVerifyDto;
import dev.gunho.api.bingous.v1.model.dto.SignUpDto;
import dev.gunho.api.bingous.v1.service.AuthService;
import dev.gunho.api.global.exception.ValidationException;
import dev.gunho.api.global.model.dto.EmailResponse;
import dev.gunho.api.global.util.RequestValidator;
import dev.gunho.api.global.util.ServiceResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
@DisplayName("AuthHandler 테스트")
class AuthHandlerTest {

    @Mock
    private AuthService authService;

    @Mock
    private RequestValidator requestValidator;

    @InjectMocks
    private AuthHandler authHandler;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        RouterFunction<ServerResponse> routerFunction = RouterFunctions.route()
                .POST("/api/v1/sign-up", authHandler::signUp)
                .POST("/api/v1/sign-up/email/verify", authHandler::verifyEmail)
                .POST("/api/v1/sign-up/email/confirm", authHandler::confirmEmail)
                .build();

        this.webTestClient = WebTestClient.bindToRouterFunction(routerFunction)
                .configureClient()
                .filter(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signUpSuccess() {
        // Given
        SignUpDto.Request request = new SignUpDto.Request(
                "testuser",
                "test@example.com",
                "testnickname",
                "password123",
                "010-1234-5678",
                null,
                null,
                false
        );

        SignUpDto.Response response = SignUpDto.Response.builder()
                .message("회원가입이 완료되었습니다.")
                .userId("testuser")
                .sessionKey("test-session-key")
                .success(true)
                .build();

        ServiceResult<SignUpDto.Response> serviceResult = ServiceResult.success(response, "회원가입이 완료되었습니다.");

        when(requestValidator.validate(any(SignUpDto.Request.class)))
                .thenReturn(Mono.just(request));
        when(authService.signUp(any(SignUpDto.Request.class), any()))
                .thenReturn(Mono.just(serviceResult));

        // When & Then
        webTestClient.post()
                .uri("/api/v1/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("auth/sign-up",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("id").description("사용자 ID"),
                                fieldWithPath("email").description("이메일 주소"),
                                fieldWithPath("nickname").description("사용자 별명"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("phoneNumber").description("전화번호").optional(),
                                fieldWithPath("gender").description("성별").optional(),
                                fieldWithPath("status").description("사용자 상태").optional(),
                                fieldWithPath("email_verified").description("이메일 인증 여부").optional()
                        ),
                        responseFields(
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("data").description("응답 데이터"),
                                fieldWithPath("data.message").description("회원가입 결과 메시지"),
                                fieldWithPath("data.userId").description("생성된 사용자 ID"),
                                fieldWithPath("data.sessionKey").description("세션 키"),
                                fieldWithPath("data.success").description("성공 여부"),
                                fieldWithPath("error").type(Object.class).description("오류 정보").optional(),
                                fieldWithPath("timestamp").description("응답 시간")
                        )
                ));
    }

    @Test
    @DisplayName("이메일 인증 코드 전송 테스트")
    void verifyEmailSuccess() {
        // Given
        EmailVerifyDto.Request request = new EmailVerifyDto.Request(
                "testuser",
                "test@example.com"
        );

        EmailResponse response = EmailResponse.builder()
                .success(true)
                .message("인증 코드가 전송되었습니다.")
                .build();

        ServiceResult<EmailResponse> serviceResult = ServiceResult.success(response, "인증 코드가 전송되었습니다.");

        when(requestValidator.validate(any(EmailVerifyDto.Request.class)))
                .thenReturn(Mono.just(request));
        when(authService.verifyEmail(any(EmailVerifyDto.Request.class)))
                .thenReturn(Mono.just(serviceResult));

        // When & Then
        webTestClient.post()
                .uri("/api/v1/sign-up/email/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("auth/email-verify",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("id").description("사용자 ID"),
                                fieldWithPath("email").description("인증할 이메일 주소")
                        ),
                        responseFields(
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("data").description("응답 데이터"),
                                fieldWithPath("data.success").description("인증 코드 전송 성공 여부"),
                                fieldWithPath("data.message").description("이메일 발송 결과 메시지"),
                                fieldWithPath("data.errorCode").description("오류 코드").optional(),
                                fieldWithPath("error").type(Object.class).description("오류 정보").optional(),
                                fieldWithPath("timestamp").description("응답 시간")
                        )
                ));
    }

    @Test
    @DisplayName("이메일 인증 코드 확인 테스트")
    void confirmEmailSuccess() {
        // Given
        EmailVerifyDto.VerifyCodeRequest request = new EmailVerifyDto.VerifyCodeRequest(
                "test@example.com",
                "123456"
        );

        EmailVerifyDto.VerifyCodeResponse response = EmailVerifyDto.VerifyCodeResponse.builder()
                .email("test@example.com")
                .verified(true)
                .message("인증에 성공했습니다.")
                .build();

        ServiceResult<EmailVerifyDto.VerifyCodeResponse> serviceResult = ServiceResult.success(response, "인증에 성공했습니다.");

        when(requestValidator.validate(any(EmailVerifyDto.VerifyCodeRequest.class)))
                .thenReturn(Mono.just(request));
        when(authService.confirmEmail(any(EmailVerifyDto.VerifyCodeRequest.class)))
                .thenReturn(Mono.just(serviceResult));

        // When & Then
        webTestClient.post()
                .uri("/api/v1/sign-up/email/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("auth/email-confirm",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("인증할 이메일 주소"),
                                fieldWithPath("code").description("인증 코드")
                        ),
                        responseFields(
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("data").description("응답 데이터"),
                                fieldWithPath("data.email").description("인증한 이메일 주소"),
                                fieldWithPath("data.verified").description("인증 성공 여부"),
                                fieldWithPath("data.message").description("인증 결과 메시지"),
                                fieldWithPath("error").type(Object.class).description("오류 정보").optional(),
                                fieldWithPath("timestamp").description("응답 시간")
                        )
                ));
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 검증 오류")
    void signUpValidationError() {
        // Given
        SignUpDto.Request request = new SignUpDto.Request(
                "", // 빈 ID
                "invalid-email", // 잘못된 이메일
                "",
                "123", // 짧은 비밀번호
                "",
                null,
                null,
                false
        );

        // ValidationException을 던지도록 Mock 설정
        Map<String, String> validationErrors = Map.of(
                "id", "사용자 ID는 필수입니다.",
                "email", "올바른 이메일 형식이 아닙니다.",
                "password", "비밀번호는 최소 8자 이상이어야 합니다."
        );

        when(requestValidator.validate(any(SignUpDto.Request.class)))
                .thenReturn(Mono.error(new ValidationException("입력값이 올바르지 않습니다.", validationErrors)));

        // When & Then
        webTestClient.post()
                .uri("/api/v1/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(document("auth/sign-up-validation-error",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("id").description("사용자 ID"),
                                fieldWithPath("email").description("이메일 주소"),
                                fieldWithPath("nickname").description("사용자 별명"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("phoneNumber").description("전화번호").optional(),
                                fieldWithPath("gender").description("성별").optional(),
                                fieldWithPath("status").description("사용자 상태").optional(),
                                fieldWithPath("email_verified").description("이메일 인증 여부").optional()
                        ),
                        responseFields(
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("data").type(Object.class).description("응답 데이터").optional(),
                                fieldWithPath("error").description("검증 오류 정보"),
                                fieldWithPath("error.id").description("ID 필드 오류 메시지").optional(),
                                fieldWithPath("error.email").description("이메일 필드 오류 메시지").optional(),
                                fieldWithPath("error.password").description("비밀번호 필드 오류 메시지").optional(),
                                fieldWithPath("timestamp").description("응답 시간")
                        )
                ));
    }

    @Test
    @DisplayName("이메일 인증 코드 전송 실패 테스트")
    void verifyEmailFailure() {
        // Given
        EmailVerifyDto.Request request = new EmailVerifyDto.Request(
                "testuser",
                "test@example.com"
        );

        when(requestValidator.validate(any(EmailVerifyDto.Request.class)))
                .thenReturn(Mono.just(request));
        when(authService.verifyEmail(any(EmailVerifyDto.Request.class)))
                .thenReturn(Mono.error(new RuntimeException("이메일 전송 실패")));

        // When & Then
        webTestClient.post()
                .uri("/api/v1/sign-up/email/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
