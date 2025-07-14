package dev.gunho.api.bingous.v1.handler;

import dev.gunho.api.bingous.v1.model.dto.EmailVerify;
import dev.gunho.api.bingous.v1.model.dto.SignUp;
import dev.gunho.api.bingous.v1.service.AuthService;
import dev.gunho.api.global.model.Result;
import dev.gunho.api.global.model.dto.EmailResponse;
import dev.gunho.api.global.util.RequestValidator;
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
                .POST("/api/v1/sign-up/email/confirm", authHandler::confirmEamil)
                .build();

        this.webTestClient = WebTestClient.bindToRouterFunction(routerFunction)
                .configureClient()
                .filter(documentationConfiguration(restDocumentation))
                .build();
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

        Result<SignUp.Response> result = Result.success(response);

        when(requestValidator.validate(any(SignUp.Request.class)))
                .thenReturn(Mono.just(request));
        when(authService.signUp(any(SignUp.Request.class), any()))
                .thenReturn(Mono.just(result));

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
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("description").description("응답 설명"),
                                fieldWithPath("timestamp").description("응답 시간"),
                                fieldWithPath("data").description("응답 데이터"),
                                fieldWithPath("data.message").description("회원가입 결과 메시지"),
                                fieldWithPath("data.userId").description("생성된 사용자 ID"),
                                fieldWithPath("data.sessionKey").description("세션 키"),
                                fieldWithPath("data.success").description("성공 여부")
                        )
                ));
    }

    @Test
    @DisplayName("이메일 인증 코드 전송 테스트")
    void verifyEmailSuccess() {
        // Given - record 생성자 사용
        EmailVerify.Request request = new EmailVerify.Request(
                "testuser",
                "test@example.com"
        );

        EmailResponse response = EmailResponse.builder()
                .success(true)
                .message("인증 코드가 전송되었습니다.")
                .build();

        when(requestValidator.validate(any(EmailVerify.Request.class)))
                .thenReturn(Mono.just(request));
        when(authService.verifyEmail(any(EmailVerify.Request.class)))
                .thenReturn(Mono.just(response));

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
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("description").description("응답 설명"),
                                fieldWithPath("timestamp").description("응답 시간"),
                                fieldWithPath("data").description("응답 데이터"),
                                fieldWithPath("data.success").description("인증 코드 전송 성공 여부"),
                                fieldWithPath("data.message").description("이메일 발송 결과 메시지"),
                                fieldWithPath("data.errorCode").description("오류 코드").optional()
                        )
                ));
    }

    @Test
    @DisplayName("이메일 인증 코드 확인 테스트")
    void confirmEmailSuccess() {
        // Given - record 생성자 사용
        EmailVerify.VerifyCodeRequest request = new EmailVerify.VerifyCodeRequest(
                "test@example.com",
                "123456"
        );

        // VerifyCodeResponse도 record이므로 생성자 사용
        EmailVerify.VerifyCodeResponse response = new EmailVerify.VerifyCodeResponse(
                "test@example.com",
                true,
                "인증에 성공했습니다."
        );

        when(requestValidator.validate(any(EmailVerify.VerifyCodeRequest.class)))
                .thenReturn(Mono.just(request));
        when(authService.confirmEmail(any(EmailVerify.VerifyCodeRequest.class)))
                .thenReturn(Mono.just(response));

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
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("description").description("응답 설명"),
                                fieldWithPath("timestamp").description("응답 시간"),
                                fieldWithPath("data").description("응답 데이터"),
                                fieldWithPath("data.email").description("인증한 이메일 주소"),
                                fieldWithPath("data.verified").description("인증 성공 여부"),
                                fieldWithPath("data.message").description("인증 결과 메시지")
                        )
                ));
    }
}
