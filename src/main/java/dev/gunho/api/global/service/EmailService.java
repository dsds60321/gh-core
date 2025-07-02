package dev.gunho.api.global.service;

import dev.gunho.api.global.model.dto.EmailRequest;
import dev.gunho.api.global.model.dto.EmailResponse;
import dev.gunho.api.global.enums.TemplateCode;
import dev.gunho.api.global.exception.EmailSendException;
import dev.gunho.api.global.repository.TemplateRepository;
import dev.gunho.api.global.util.EmailTemplateUtil;
import dev.gunho.api.global.util.Util;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateRepository templateRepository;
    private final EmailTemplateUtil templateUtil;

    public Mono<EmailResponse> sendEmail(TemplateCode templateCode, EmailRequest emailRequest, List<String> variables) {
        // 수정된 메서드명 사용
        return templateRepository.findByNameAndIsActive(templateCode.getName(), true)
                .flatMap(template -> {
                    String subject = template.getSubject();
                    String content = Util.MsgUtil.getMessage(template.getContent(), variables);

                    EmailRequest email = EmailRequest.builder()
                            .to(emailRequest.getTo())
                            .subject(subject)
                            .content(content)
                            .isHtml(true)
                            .build();

                    return sendEmail(email);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<EmailResponse> sendEmail(EmailRequest emailRequest) {
        return Mono.fromCallable(() -> {
                    try {
                        MimeMessage message = mailSender.createMimeMessage();
                        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                        helper.setTo(emailRequest.getTo());
                        helper.setSubject(emailRequest.getSubject());
                        helper.setText(emailRequest.getContent(), emailRequest.isHtml());

                        mailSender.send(message);

                        log.info("Email sent successfully to: {}", emailRequest.getTo());

                        return EmailResponse.builder()
                                .success(true)
                                .message("이메일이 성공적으로 발송되었습니다.")
                                .build();
                    } catch (Exception e) {
                        log.error("Failed to send email to: {}", emailRequest.getTo(), e);
                        throw new RuntimeException("이메일 발송 실패", e);
                    }
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(ex -> new EmailSendException("이메일 발송 실패", ex));
    }

    public Mono<EmailResponse> sendTemplateEmail(TemplateCode templateCode, List<String> variables, String to) {
        return Mono.zip(
                        templateUtil.generateContent(templateCode, variables),
                        templateUtil.getSubject(templateCode)
                )
                .flatMap(tuple -> {
                    String content = tuple.getT1();
                    String subject = tuple.getT2();

                    EmailRequest request = EmailRequest.builder()
                            .to(to)
                            .subject(subject)
                            .content(content)
                            .isHtml(true)
                            .build();
                    return sendEmail(request);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}
