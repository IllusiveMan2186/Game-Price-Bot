package com.gpb.game.service.impl.notification;

import com.gpb.common.entity.event.NotificationEvent;
import com.gpb.common.entity.game.GameInStoreDto;
import com.gpb.common.util.CommonConstants;
import com.gpb.game.entity.user.BasicUser;
import com.gpb.game.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "TELEGRAM")
@Slf4j
@AllArgsConstructor
public class TelegramNotificationServiceImpl implements NotificationService {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @Override
    public void sendGameInfoChange(BasicUser user, List<GameInStoreDto> gameInShopList) {
        log.info("Email notification for user '{}'", user.getId());
        kafkaTemplate.send(CommonConstants.TELEGRAM_NOTIFICATION_TOPIC,
                "1",
                new NotificationEvent(user.getId(), gameInShopList));
    }
}
