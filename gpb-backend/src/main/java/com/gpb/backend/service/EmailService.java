package com.gpb.backend.service;

import com.gpb.backend.entity.UserActivation;
import com.gpb.backend.entity.WebUser;
import com.gpb.common.entity.event.NotificationEvent;

/**
 * Service interface for handling email-related operations.
 * <p>
 * Implementations of this interface are responsible for sending different types of emails,
 * such as game information change notifications and email verification messages.
 * </p>
 */
public interface EmailService {

    /**
     * Sends an email notification about game information changes to a specified user.
     *
     * @param user                   the {@link WebUser} to which the notification is sent
     * @param emailNotificationEvent the {@link NotificationEvent} containing details of the game info change
     */
    void sendGameInfoChange(WebUser user, NotificationEvent emailNotificationEvent);

    /**
     * Sends an email verification message to the user.
     *
     * @param userActivation the {@link UserActivation} instance containing activation details
     */
    void sendEmailVerification(UserActivation userActivation);
}