package dev.gunho.api.global.repository;

import dev.gunho.api.global.model.entity.TemplateEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface TemplateRepository extends R2dbcRepository<TemplateEntity, Long> {


    Mono<TemplateEntity> findByName(String name);
    Mono<TemplateEntity> findByNameAndIsActive(String name, boolean active);
}
