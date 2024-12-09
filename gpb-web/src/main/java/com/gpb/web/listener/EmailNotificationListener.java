package com.gpb.web.listener;

import com.gpb.web.bean.event.EmailNotificationEvent;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.service.EmailService;
import com.gpb.web.service.UserService;
import com.gpb.web.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class EmailNotificationListener {

    private final UserService userService;
    private final EmailService emailService;

    public EmailNotificationListener(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @KafkaListener(topics = Constants.EMAIL_NOTIFICATION_TOPIC, groupId = Constants.GPB_KAFKA_GROUP_ID)
    @Transactional
    public void listenEmailNotification(ConsumerRecord<String, EmailNotificationEvent> unfollowRecord) {
        EmailNotificationEvent event = unfollowRecord.value();
        log.info("Request for email notification user {} ", event.getBasicUserId());
        WebUser user = userService.getUserBasicUserById(event.getBasicUserId());
        emailService.sendGameInfoChange(user, event);
    }
}
