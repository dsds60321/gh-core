package dev.gunho.api.stock.v1.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "stock.twelvedata")
public class TwelveProperties {

    @NotBlank
    private String apiKey;

    private String baseUrl = "https://api.twelvedata.com";

    private Duration connectTimeout = Duration.ofSeconds(5);

    private Duration readTimeout = Duration.ofSeconds(10);

    private Duration retryBackoff = Duration.ofSeconds(2);

    @Min(0)
    private int maxRetry = 2;

    @Min(1)
    private int symbolChunkSize = 8;

}
