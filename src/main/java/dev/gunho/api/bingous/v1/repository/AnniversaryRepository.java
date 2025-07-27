package dev.gunho.api.bingous.v1.repository;

import dev.gunho.api.bingous.v1.model.entity.AppSession;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AnniversaryRepository extends ReactiveCrudRepository<AppSession, Long> {


}
