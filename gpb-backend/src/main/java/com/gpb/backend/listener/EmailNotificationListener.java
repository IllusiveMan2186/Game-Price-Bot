package com.gpb.backend.listener;

import com.gpb.backend.bean.event.EmailNotificationEvent;
import com.gpb.backend.bean.user.WebUser;
import com.gpb.backend.service.EmailService;
import com.gpb.backend.service.UserManagementService;
import com.gpb.backend.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class EmailNotificationListener {

    private final UserManagementService userService;
    private final EmailService emailService;

    public EmailNotificationListener(UserManagementService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @KafkaListener(topics = Constants.EMAIL_NOTIFICATION_TOPIC,
            groupId = Constants.GPB_KAFKA_GROUP_ID,
            containerFactory = "notificationListener")
    @Transactional
    public void listenEmailNotification(ConsumerRecord<String, EmailNotificationEvent> unfollowRecord) {
        EmailNotificationEvent event = unfollowRecord.value();
        log.info("Request for email notification user {} ", event.getBasicUserId());
        WebUser user = userService.getUserByBasicUserId(event.getBasicUserId());
        emailService.sendGameInfoChange(user, event);
    }
}
