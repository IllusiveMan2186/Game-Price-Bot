package com.gpb.game.resolver;

import com.gpb.common.entity.user.UserNotificationType;
import com.gpb.game.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Resolves the appropriate {@link NotificationService} implementation
 * based on the given {@link UserNotificationType}.
 */
@Component
@AllArgsConstructor
public class NotificationServiceResolver {

    private final Map<String, NotificationService> notificationServices;

    /**
     * Retrieves the appropriate {@link NotificationService} based on the given type.
     *
     * @param type the type of user notification (EMAIL, TELEGRAM)
     * @return the matching {@link NotificationService} implementation
     */
    public NotificationService getService(UserNotificationType type) {
        return notificationServices.get(type.name());
    }
}