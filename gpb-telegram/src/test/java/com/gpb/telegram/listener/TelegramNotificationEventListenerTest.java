package com.gpb.telegram.listener;

import com.gpb.common.entity.event.NotificationEvent;
import com.gpb.common.entity.game.GameInStoreDto;
import com.gpb.common.util.CommonConstants;
import com.gpb.telegram.bot.GamePriceBot;
import com.gpb.telegram.entity.TelegramUser;
import com.gpb.telegram.mapper.GameInStoreMapper;
import com.gpb.telegram.service.TelegramUserService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelegramNotificationEventListenerTest {

    @Mock
    GameInStoreMapper gameInStoreMapper;
    @Mock
    GamePriceBot bot;
    @Mock
    TelegramUserService telegramUserService;
    @Mock
    MessageSource messageSource;

    @InjectMocks
    TelegramNotificationEventListener listener;

    @Test
    void testListenNotification_whenSuccess_shouldSendNotification() {
        Locale locale = Locale.ENGLISH;
        String titleMessage = "title";
        long basicUserId = 2L;
        long telegramId = 3L;

        TelegramUser user = TelegramUser.builder().telegramId(telegramId).locale(locale).build();
        List<GameInStoreDto> gameInStoreDtoList = new ArrayList<>();
        gameInStoreDtoList.add(GameInStoreDto.builder().build());
        NotificationEvent event = new NotificationEvent(basicUserId, gameInStoreDtoList);
        ConsumerRecord<String, NotificationEvent> notificationEvent =
                new ConsumerRecord<>(CommonConstants.TELEGRAM_NOTIFICATION_TOPIC, 0, 0, null, event);
        SendMessage expectedGameInStoreNotification = new SendMessage();

        when(telegramUserService.getByBasicUserId(basicUserId)).thenReturn(user);
        when(messageSource.getMessage("game.change.info.notification", null, locale)).thenReturn(titleMessage);
        when(gameInStoreMapper.mapGameInStoreNotificationToTelegramPage(
                String.valueOf(user.getTelegramId()),
                gameInStoreDtoList.get(0),
                user.getLocale())).thenReturn(expectedGameInStoreNotification);


        listener.listenNotification(notificationEvent);


        List<PartialBotApiMethod> messages = new ArrayList<>();
        messages.add(SendMessage.builder()
                .chatId(telegramId)
                .text(titleMessage)
                .build());
        messages.add(expectedGameInStoreNotification);
        verify(bot).sendNotification(messages);
    }
}