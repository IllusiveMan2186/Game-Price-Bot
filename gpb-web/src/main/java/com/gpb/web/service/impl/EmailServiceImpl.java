package com.gpb.web.service.impl;

import com.gpb.web.bean.EmailEvent;
import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.user.UserActivation;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.service.EmailService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.List;

import static com.gpb.web.util.Constants.EMAIL_SERVICE_TOPIC;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {


    private KafkaTemplate<Long, EmailEvent> kafkaTemplate;

    @Value("${WEB_SERVICE_URL}")
    private String webServiceUrl;
    
    public EmailServiceImpl(KafkaTemplate<Long, EmailEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendGameInfoChange(WebUser user, List<GameInShop> gameInShopList) {
        Context context = new Context();
        context.setVariable("games", gameInShopList);
        context.setLocale(user.getLocale());
        sendEmail(user.getEmail(), "Game info changes", context, "email-info-changed-template");
    }

    @Override
    public void sendEmailVerification(UserActivation userActivation) {
        Context context = new Context();
        context.setVariable("url", webServiceUrl + "/email/" + userActivation.getToken());
        context.setLocale(userActivation.getUser().getLocale());
        sendEmail(userActivation.getUser().getEmail(), "User verification", context, "email-user-verification");
    }

    @PostConstruct
    void post() {
        sendEmail("REDACTED", "sadsa", new Context(), "email-user-verification");
    }
    private void sendEmail(String to, String subject, Context context, String templateName) {
        log.info(String.format("Email event for recipient '%s' about '%s'", to, subject));
        kafkaTemplate.send(EMAIL_SERVICE_TOPIC,1L, new EmailEvent(to, subject, context, templateName));
    }
}
