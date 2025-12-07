package dev.gunho.api.stock.v1.scheduler;

import dev.gunho.api.stock.v1.service.TwelveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TwelveScheduler {

    private final TwelveService twelveService;
    // 미국 기준  15:00
//    @Scheduled(cron = "0 0 17 * * MON-FRI", zone = "America/New_York")
    @Scheduled(cron = "0 23 00 * * MON-SUN", zone = "Asia/Seoul")
    public void saveDailyStock() {
        log.info("뉴욕 기중 17시 dailyStock 데이터 동기화 시작");
        twelveService.saveDailyStock()
                .doOnError(ex -> log.error("[saveDailyStock] ERROR : {} ", ex.getMessage()))
                .block();
    }
}
