package com.gpb.stores.service.impl;

import com.gpb.stores.bean.EmailEvent;
import com.gpb.stores.bean.game.GameInShop;
import com.gpb.stores.bean.user.WebUser;
import com.gpb.stores.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.gpb.stores.util.Constants.EMAIL_SERVICE_TOPIC;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {


    private final KafkaTemplate<Long, EmailEvent> kafkaTemplate;

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
