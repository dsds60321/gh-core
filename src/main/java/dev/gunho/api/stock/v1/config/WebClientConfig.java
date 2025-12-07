package dev.gunho.api.stock.v1.config;

import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Configuration
@EnableConfigurationProperties(TwelveProperties.class)
public class WebClientConfig {

    @Bean
    public WebClient twelveWebClient(WebClient.Builder webClient, TwelveProperties twelveProperties) {
        HttpClient client = HttpClient
                .create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) twelveProperties.getConnectTimeout().toMillis())
                .responseTimeout(twelveProperties.getReadTimeout());

        return webClient.clone()
                .baseUrl(twelveProperties.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(client))
                .build();
    }
}
