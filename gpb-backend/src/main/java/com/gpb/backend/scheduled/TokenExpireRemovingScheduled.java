package com.gpb.backend.scheduled;

import com.gpb.backend.service.RefreshTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class TokenExpireRemovingScheduled {

    private RefreshTokenService refreshTokenService;

    @Scheduled(cron = "0 0 * * * *")
    public void deleteExpiredTokens() {
        log.trace("Scheduled job: removing expired tokens");
        refreshTokenService.deleteExpiredTokens();
    }
}
