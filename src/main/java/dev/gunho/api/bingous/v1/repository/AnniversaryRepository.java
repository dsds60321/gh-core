package dev.gunho.api.bingous.v1.repository;

import dev.gunho.api.bingous.v1.model.entity.Anniversary;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AnniversaryRepository extends ReactiveCrudRepository<Anniversary, Long> {

}
