package com.gpb.game.service.impl.notification;

import com.gpb.common.entity.event.NotificationEvent;
import com.gpb.common.entity.game.GameInStoreDto;
import com.gpb.common.util.CommonConstants;
import com.gpb.game.entity.user.BasicUser;
import com.gpb.game.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "EMAIL")
@Slf4j
public class EmailNotificationServiceImpl implements NotificationService {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public EmailNotificationServiceImpl(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendGameInfoChange(BasicUser user, List<GameInStoreDto> gameInShopList) {
        log.info("Email notification for user '{}'", user.getId());
        kafkaTemplate.send(CommonConstants.EMAIL_NOTIFICATION_TOPIC,
                "1",
                new NotificationEvent(user.getId(), gameInShopList));
    }
}
