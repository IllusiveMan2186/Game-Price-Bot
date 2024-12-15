package com.gpb.game.unit.service.impl.natification;

import com.gpb.game.bean.event.EmailNotificationEvent;
import com.gpb.game.bean.game.GameInShop;
import com.gpb.game.bean.user.BasicUser;
import com.gpb.game.service.impl.notification.EmailNotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.Map;

import static com.gpb.game.util.Constants.EMAIL_NOTIFICATION_TOPIC;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

class EmailNotificationServiceImplTest {

    @Mock
    private KafkaTemplate<String, EmailNotificationEvent> kafkaTemplate;

    @InjectMocks
    private EmailNotificationServiceImpl emailNotificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendGameInfoChange_shouldSendEmailNotification() {
        BasicUser user = new BasicUser();
        user.setId(123L);

        GameInShop game1 = new GameInShop();
        game1.setId(1L);
        GameInShop game2 = new GameInShop();
        game2.setId(2L);

        List<GameInShop> gameInShopList = List.of(game1, game2);


        emailNotificationService.sendGameInfoChange(user, gameInShopList);

        
        EmailNotificationEvent expectedEvent = new EmailNotificationEvent(
                user.getId(),
                Map.of("games", gameInShopList)
        );

        verify(kafkaTemplate).send(EMAIL_NOTIFICATION_TOPIC, "1", expectedEvent);
    }

    @Test
    void sendGameInfoChange_shouldLogInfo() {
        BasicUser user = new BasicUser();
        user.setId(123L);

        GameInShop game = new GameInShop();
        game.setId(1L);

        List<GameInShop> gameInShopList = List.of(game);


        emailNotificationService.sendGameInfoChange(user, gameInShopList);


        verify(kafkaTemplate).send(eq(EMAIL_NOTIFICATION_TOPIC), eq("1"), any(EmailNotificationEvent.class));
    }
}
