package com.gpb.backend.service.impl;

import com.gpb.backend.bean.event.EmailEvent;
import com.gpb.backend.bean.event.EmailNotificationEvent;
import com.gpb.backend.bean.user.UserActivation;
import com.gpb.backend.bean.user.WebUser;
import com.gpb.backend.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.gpb.backend.util.Constants.EMAIL_SERVICE_TOPIC;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {


    private final KafkaTemplate<String, EmailEvent> kafkaTemplate;

    @Value("${BACKEND_SERVICE_URL}")
    private String webServiceUrl;

    public EmailServiceImpl(KafkaTemplate<String, EmailEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendGameInfoChange(WebUser user, EmailNotificationEvent emailNotificationEvent) {
        sendEmail(user.getEmail(), "Game info changes", emailNotificationEvent.getVariables(), user.getLocale(),
                "email-info-changed-template");
    }

    @Override
    public void sendEmailVerification(UserActivation userActivation) {
        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("url", webServiceUrl + "/email/" + userActivation.getToken());
        Locale locale = userActivation.getUser().getLocale() != null
                ? userActivation.getUser().getLocale()
                : Locale.getDefault();
        sendEmail(userActivation.getUser().getEmail(), "User verification", variables, locale,
                "email-user-verification");
    }

    private void sendEmail(String to, String subject, Map<String, Object> variables, Locale locale, String templateName) {
        log.info(String.format("Email event for recipient '%s' about '%s'", to, subject));
        String key = UUID.randomUUID().toString();
        kafkaTemplate.send(EMAIL_SERVICE_TOPIC, key, new EmailEvent(to, subject, variables, locale, templateName));
    }
}
