package com.gpb.game.unit.service.impl.notification;

import com.gpb.common.entity.event.NotificationEvent;
import com.gpb.common.entity.game.GameInStoreDto;
import com.gpb.common.util.CommonConstants;
import com.gpb.game.entity.user.BasicUser;
import com.gpb.game.service.impl.notification.EmailNotificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailNotificationServiceImplTest {

    @Mock
    private KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @InjectMocks
    private EmailNotificationServiceImpl emailNotificationService;

    @Test
    void testSendGameInfoChange_whenSuccess_shouldSendEmailNotification() {
        BasicUser user = new BasicUser();
        user.setId(123L);

        GameInStoreDto game1 = new GameInStoreDto();
        game1.setId(1L);
        GameInStoreDto game2 = new GameInStoreDto();
        game2.setId(2L);

        List<GameInStoreDto> gameInShopList = List.of(game1, game2);


        emailNotificationService.sendGameInfoChange(user, gameInShopList);


        NotificationEvent expectedEvent = new NotificationEvent(user.getId(), gameInShopList);

        verify(kafkaTemplate).send(CommonConstants.EMAIL_NOTIFICATION_TOPIC, "1", expectedEvent);
    }
}
