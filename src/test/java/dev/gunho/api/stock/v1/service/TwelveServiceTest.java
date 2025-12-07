package dev.gunho.api.stock.v1.service;

import dev.gunho.api.stock.v1.client.TwelveClient;
import dev.gunho.api.stock.v1.config.TwelveProperties;
import dev.gunho.api.stock.v1.entity.StockDailyEntity;
import dev.gunho.api.stock.v1.entity.StockMetaEntity;
import dev.gunho.api.stock.v1.repository.StockDailyRepository;
import dev.gunho.api.stock.v1.repository.StockMetaRepository;
import io.netty.channel.ChannelOption;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TwelveServiceTest {

    private static final String ENV_API_KEY = "79d2088a9a614afdbb25a5a009438a61";

    @Mock
    private StockMetaRepository stockMetaRepository;

    @Mock
    private StockDailyRepository stockDailyRepository;

    @Test
    @DisplayName("saveDailyStock은 실제 TwelveData API 결과를 저장한다")
    void saveDailyStock_savesDataThroughRealTwelveApi() {
        String apiKey = System.getenv(ENV_API_KEY);
//        Assumptions.assumeTrue(apiKey != null && !apiKey.isBlank(),
//                "TWELVEDATA_API_KEY 환경 변수가 설정되어야 테스트를 실행합니다.");

        TwelveProperties properties = createProperties(apiKey);
        TwelveClient twelveClient = createRealClient(properties);
        TwelveService twelveService = new TwelveService(stockMetaRepository, stockDailyRepository, twelveClient);

        StockMetaEntity stockMeta = StockMetaEntity.builder()
                .symbol("AAPL")
                .isEtf(false)
                .build();

        when(stockMetaRepository.findByActive())
                .thenReturn(Flux.just(stockMeta));

        when(stockDailyRepository.save(any()))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(twelveService.saveDailyStock())
                .expectSubscription()
                .verifyComplete();

        ArgumentCaptor<StockDailyEntity> captor = ArgumentCaptor.forClass(StockDailyEntity.class);
        verify(stockDailyRepository, atLeastOnce()).save(captor.capture());

        List<StockDailyEntity> savedEntities = captor.getAllValues();
        assertThat(savedEntities).isNotEmpty();
        StockDailyEntity latest = savedEntities.get(savedEntities.size() - 1);
        assertThat(latest.getSymbol()).isEqualTo("AAPL");
        assertThat(latest.getClosePrice()).isNotNull();
        assertThat(latest.getTradeDate()).isNotNull();
    }

    private TwelveProperties createProperties(String apiKey) {
        TwelveProperties properties = new TwelveProperties();
        properties.setApiKey(apiKey);
        properties.setBaseUrl("https://api.twelvedata.com");
        properties.setConnectTimeout(Duration.ofSeconds(5));
        properties.setReadTimeout(Duration.ofSeconds(10));
        properties.setRetryBackoff(Duration.ofSeconds(1));
        properties.setMaxRetry(1);
        return properties;
    }

    private TwelveClient createRealClient(TwelveProperties properties) {
        HttpClient client = HttpClient
                .create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) properties.getConnectTimeout().toMillis())
                .responseTimeout(properties.getReadTimeout());

        WebClient webClient = WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(client))
                .build();

        return new TwelveClient(webClient, properties);
    }
}
