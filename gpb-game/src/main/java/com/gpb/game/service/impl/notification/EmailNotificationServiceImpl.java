package com.gpb.game.service.impl.notification;

import com.gpb.game.bean.event.EmailNotificationEvent;
import com.gpb.game.bean.game.GameInShop;
import com.gpb.game.bean.user.BasicUser;
import com.gpb.game.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.gpb.game.util.Constants.EMAIL_NOTIFICATION_TOPIC;

@Service(value = "EMAIL")
@Slf4j
public class EmailNotificationServiceImpl implements NotificationService {

    private final KafkaTemplate<String, EmailNotificationEvent> kafkaTemplate;

    public EmailNotificationServiceImpl(KafkaTemplate<String, EmailNotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendGameInfoChange(BasicUser user, List<GameInShop> gameInShopList) {
        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("games", gameInShopList);
        log.info(String.format("Email notification for user '%s'", user.getId()));
        kafkaTemplate.send(EMAIL_NOTIFICATION_TOPIC, "1", new EmailNotificationEvent(user.getId(), variables));
    }
}
