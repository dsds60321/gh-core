package dev.gunho.api.global.util;

import dev.gunho.api.global.enums.TemplateCode;
import dev.gunho.api.global.model.entity.TemplateEntity;
import dev.gunho.api.global.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailTemplateUtil {

    private final TemplateRepository templateRepository;


    public Mono<String> generateContent(TemplateCode templateCode, List<String> variables) {
        return templateRepository.findByNameAndIsActive(templateCode.getName(), true)
                .map(template -> Util.MsgUtil.getMessage(template.getContent(), variables))
                .doOnNext(content -> log.debug("Generated content for template {}: {}", templateCode.getName(), content))
                .doOnError(error -> log.error("Failed to generate content for template: {}", templateCode.getName(), error));
    }


    public Mono<String> getSubject(TemplateCode templateCode) {
        return templateRepository.findByNameAndIsActive(templateCode.getName(), true)
                .map(TemplateEntity::getSubject)
                .doOnNext(subject -> log.debug("Retrieved subject for template {}: {}", templateCode.getName(), subject))
                .doOnError(error -> log.error("Failed to get subject for template: {}", templateCode.getName(), error));
    }

}
