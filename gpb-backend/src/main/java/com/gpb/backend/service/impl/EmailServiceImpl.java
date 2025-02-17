package com.gpb.backend.service.impl;

import com.gpb.backend.entity.EmailChanging;
import com.gpb.backend.entity.UserActivation;
import com.gpb.backend.entity.WebUser;
import com.gpb.backend.service.EmailService;
import com.gpb.common.entity.event.EmailEvent;
import com.gpb.common.entity.event.NotificationEvent;
import com.gpb.common.util.CommonConstants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
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
    private final MessageSource messageSource;

    @Value("${FRONT_SERVICE_URL}")
    private String frontendServiceUrl;

    public EmailServiceImpl(KafkaTemplate<String, EmailEvent> kafkaTemplate, MessageSource messageSource) {
        this.kafkaTemplate = kafkaTemplate;
        this.messageSource = messageSource;
    }

    @Override
    public void sendGameInfoChange(WebUser user, NotificationEvent notificationEvent) {
        log.info("Send game info changed  to user {}", user.getEmail());
        String subject = messageSource.getMessage("email.subject.game.info.change", null, user.getLocale());
        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("games", notificationEvent.getGameInShopList());
        sendEmail(user.getEmail(), subject, variables, user.getLocale(), "email-info-changed-template");
    }

    @Override
    public void sendEmailVerification(UserActivation userActivation) {
        log.info("Send email verification to user {}", userActivation.getUser().getEmail());
        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("url", frontendServiceUrl + "/activation?token=" + userActivation.getToken());
        Locale locale = userActivation.getUser().getLocale() != null
                ? userActivation.getUser().getLocale()
                : Locale.getDefault();
        String subject = messageSource.getMessage("email.subject.user.verification", null, locale);

        sendEmail(userActivation.getUser().getEmail(), subject, variables, locale, "email-user-verification");
    }

    @Override
    public void sendEmailChange(EmailChanging emailChanging) {
        WebUser user = emailChanging.getUser();
        Locale locale = user.getLocale();

        sendEmailChangeWithToken(emailChanging.getNewEmail(), emailChanging.getNewEmailToken(), locale);
        sendEmailChangeWithToken(user.getEmail(), emailChanging.getOldEmailToken(), locale);
    }

    private void sendEmailChangeWithToken(String email, String token, Locale locale) {
        Map<String, Object> variables = new LinkedHashMap<>();
        String subject = messageSource.getMessage("email.subject.email.change", null, locale);

        variables.put("url", frontendServiceUrl + "/email/change/confirm?token=" + token);

        sendEmail(email, subject, variables, locale, "email-changing-email");
    }

    private void sendEmail(String to, String subject, Map<String, Object> variables, Locale locale, String templateName) {
        log.info("Email event for recipient '{}' about '{}'", to, subject);
        String key = UUID.randomUUID().toString();
        kafkaTemplate.send(CommonConstants.EMAIL_SERVICE_TOPIC, key, new EmailEvent(to, subject, variables, locale, templateName));
    }
}
