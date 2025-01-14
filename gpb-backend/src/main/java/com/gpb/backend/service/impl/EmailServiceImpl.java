package com.gpb.backend.service.impl;

import com.gpb.backend.entity.UserActivation;
import com.gpb.backend.entity.WebUser;
import com.gpb.backend.service.EmailService;
import com.gpb.common.entity.event.EmailEvent;
import com.gpb.common.entity.event.EmailNotificationEvent;
import com.gpb.common.util.CommonConstants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


@Service
@Slf4j
@Data
public class EmailServiceImpl implements EmailService {


    private final KafkaTemplate<String, EmailEvent> kafkaTemplate;

    @Value("${FRONT_SERVICE_URL}")
    private String frontendServiceUrl;//url for user activation link

    public EmailServiceImpl(KafkaTemplate<String, EmailEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendGameInfoChange(WebUser user, EmailNotificationEvent emailNotificationEvent) {
        log.info("Send game info changed  to user {}", user.getEmail());
        sendEmail(user.getEmail(), "Game info changes", emailNotificationEvent.getVariables(), user.getLocale(),
                "email-info-changed-template");
    }

    @Override
    public void sendEmailVerification(UserActivation userActivation) {
        log.info("Send email verification to user {}", userActivation.getUser().getEmail());
        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("url", frontendServiceUrl + "/activation?token=" + userActivation.getToken());
        Locale locale = userActivation.getUser().getLocale() != null
                ? userActivation.getUser().getLocale()
                : Locale.getDefault();
        sendEmail(userActivation.getUser().getEmail(), "User verification", variables, locale,
                "email-user-verification");
    }

    private void sendEmail(String to, String subject, Map<String, Object> variables, Locale locale, String templateName) {
        log.info("Email event for recipient '{}' about '{}'", to, subject);
        String key = UUID.randomUUID().toString();
        kafkaTemplate.send(CommonConstants.EMAIL_SERVICE_TOPIC, key, new EmailEvent(to, subject, variables, locale, templateName));
    }
}
