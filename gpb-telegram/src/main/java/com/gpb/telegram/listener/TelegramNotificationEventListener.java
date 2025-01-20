package com.gpb.telegram.listener;

import com.gpb.common.entity.event.NotificationEvent;
import com.gpb.common.entity.game.GameInStoreDto;
import com.gpb.common.util.CommonConstants;
import com.gpb.telegram.bot.GamePriceBot;
import com.gpb.telegram.entity.TelegramUser;
import com.gpb.telegram.mapper.GameInStoreMapper;
import com.gpb.telegram.service.TelegramUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.MessageSource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
@AllArgsConstructor
public class TelegramNotificationEventListener {

    private final GameInStoreMapper gameInStoreMapper;
    private final GamePriceBot bot;
    private final TelegramUserService telegramUserService;
    private final MessageSource messageSource;


    @KafkaListener(topics = CommonConstants.TELEGRAM_NOTIFICATION_TOPIC,
            groupId = CommonConstants.GPB_KAFKA_GROUP_ID,
            containerFactory = "notificationListener")
    public void listenNotification(ConsumerRecord<String, NotificationEvent> notificationEvent) {
        NotificationEvent event = notificationEvent.value();
        log.info("Request for notification user {} ", event.getBasicUserId());
        TelegramUser user = telegramUserService.getByBasicUserId(event.getBasicUserId());
        List<PartialBotApiMethod> messages = new ArrayList<>();

        messages.add(getGameInfoChangingTitle(user.getTelegramId(), user.getLocale()));

        event.getGameInShopList().forEach(game -> messages.add(getGameChangeInfo(game, user)));

        bot.sendNotification(messages);
    }

    private SendMessage getGameInfoChangingTitle(long chatId, Locale locale) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(messageSource.getMessage("game.change.info.notification", null, locale))
                .build();
    }

    private SendMessage getGameChangeInfo(GameInStoreDto game, TelegramUser user) {
        return gameInStoreMapper.mapGameInStoreNotificationToTelegramPage(
                String.valueOf(user.getTelegramId()),
                game,
                user.getLocale());
    }
}
