package com.gpb.game.unit.resolver;

import com.gpb.common.entity.user.UserNotificationType;
import com.gpb.game.resolver.NotificationServiceResolver;
import com.gpb.game.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class NotificationServiceResolverTest {

    private NotificationServiceResolver resolver;
    private NotificationService emailService;
    private NotificationService telegramService;

    @BeforeEach
    void setUp() {
        emailService = mock(NotificationService.class);
        telegramService = mock(NotificationService.class);
        Map<String, NotificationService> services = new HashMap<>();
        services.put(UserNotificationType.EMAIL.name(), emailService);
        services.put(UserNotificationType.TELEGRAM.name(), telegramService);

        resolver = new NotificationServiceResolver(services);
    }

    @Test
    void testGetService_whenValidType_shouldReturnService() {
        NotificationService result = resolver.getService(UserNotificationType.EMAIL);
        assertThat(result).isEqualTo(emailService);

        result = resolver.getService(UserNotificationType.TELEGRAM);


        assertThat(result).isEqualTo(telegramService);
    }
}
