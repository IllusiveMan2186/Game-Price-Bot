package com.gpb.web.service.impl;

import com.gpb.web.bean.EmailEvent;
import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.gpb.web.util.Constants.EMAIL_SERVICE_TOPIC;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {


    private final KafkaTemplate<Long, EmailEvent> kafkaTemplate;

    @Value("${WEB_SERVICE_URL}")
    private String webServiceUrl;

    public EmailServiceImpl(KafkaTemplate<Long, EmailEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendGameInfoChange(WebUser user, List<GameInShop> gameInShopList) {
        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("games", gameInShopList);
        sendEmail(user.getEmail(), "Game info changes", variables, user.getLocale(),
                "email-info-changed-template");
    }

    private void sendEmail(String to, String subject, Map<String, Object> variables, Locale locale, String templateName) {
        log.info(String.format("Email event for recipient '%s' about '%s'", to, subject));
        kafkaTemplate.send(EMAIL_SERVICE_TOPIC, 1L, new EmailEvent(to, subject, variables, locale, templateName));
    }
}
