package com.gpb.backend.unit.scheduled;

import com.gpb.backend.scheduled.TokenExpireRemovingScheduled;
import com.gpb.backend.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class TokenExpireRemovingScheduledTest {

    private RefreshTokenService refreshTokenService;
    private TokenExpireRemovingScheduled scheduledTask;

    @BeforeEach
    void setUp() {
        refreshTokenService = mock(RefreshTokenService.class);
        scheduledTask = new TokenExpireRemovingScheduled(refreshTokenService);
    }

    @Test
    void testDeleteExpiredTokens_whenTime_shouldInvokeServiceMethod() {
        scheduledTask.deleteExpiredTokens();

        verify(refreshTokenService, times(1)).deleteExpiredTokens();
    }
}
