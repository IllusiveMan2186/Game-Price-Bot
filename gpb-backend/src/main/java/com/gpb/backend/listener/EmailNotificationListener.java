package com.gpb.backend.listener;

import com.gpb.backend.entity.WebUser;
import com.gpb.backend.service.EmailService;
import com.gpb.backend.service.UserManagementService;
import com.gpb.common.entity.event.NotificationEvent;
import com.gpb.common.util.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Listener for email notification events.
 * <p>
 * This listener consumes {@link NotificationEvent} messages from the Kafka topic defined by
 * {@link CommonConstants#EMAIL_NOTIFICATION_TOPIC}. Upon receiving a notification event, it retrieves the
 * corresponding user and triggers the email service to send a game info change notification.
 * </p>
 */
@Slf4j
@Component
public class EmailNotificationListener {

    private final UserManagementService userService;
    private final EmailService emailService;

    public EmailNotificationListener(final UserManagementService userService, final EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    /**
     * Listens for email notification events from Kafka and processes them.
     *
     * @param record the Kafka consumer record containing the {@link NotificationEvent}
     */
    @KafkaListener(
            topics = CommonConstants.EMAIL_NOTIFICATION_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "notificationListener"
    )
    @Transactional
    public void listenEmailNotification(final ConsumerRecord<String, NotificationEvent> record) {
        final NotificationEvent event = record.value();
        log.info("Received email notification request for user with basicUserId: {}", event.getBasicUserId());

        final WebUser user = userService.getUserByBasicUserId(event.getBasicUserId());
        if (user == null) {
            log.warn("User with basicUserId {} not found; skipping email notification", event.getBasicUserId());
            return;
        }

        emailService.sendGameInfoChange(user, event);
        log.info("Email notification sent to user with basicUserId: {}", event.getBasicUserId());
    }
}