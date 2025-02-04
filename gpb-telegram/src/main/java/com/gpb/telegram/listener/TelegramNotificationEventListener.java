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

/**
 * Listener for processing Telegram notification events received from Kafka.
 * <p>
 * This class listens for {@link NotificationEvent} messages on the topic defined in {@link CommonConstants#TELEGRAM_NOTIFICATION_TOPIC}.
 * Upon receiving an event, it retrieves the corresponding Telegram user, prepares a list of messages including a notification title and
 * details for each game in the event, and then sends these messages using the {@link GamePriceBot}.
 * </p>
 */
@Slf4j
@Component
@AllArgsConstructor
public class TelegramNotificationEventListener {

    private final GameInStoreMapper gameInStoreMapper;
    private final GamePriceBot bot;
    private final TelegramUserService telegramUserService;
    private final MessageSource messageSource;

    /**
     * Processes a notification event from Kafka.
     *
     * @param notificationEvent the Kafka consumer record containing a {@link NotificationEvent}
     */
    @KafkaListener(
            topics = CommonConstants.TELEGRAM_NOTIFICATION_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "notificationListener"
    )
    public void listenNotification(final ConsumerRecord<String, NotificationEvent> notificationEvent) {
        final NotificationEvent event = notificationEvent.value();
        log.info("Received notification request for user with basicUserId: {}", event.getBasicUserId());

        final TelegramUser user = telegramUserService.getByBasicUserId(event.getBasicUserId());
        final List<PartialBotApiMethod> messages = new ArrayList<>();

        messages.add(getGameInfoChangingTitle(user.getTelegramId(), user.getLocale()));

        event.getGameInShopList().forEach(game ->
                messages.add(getGameChangeInfo(game, user))
        );

        bot.sendNotification(messages);
    }

    /**
     * Constructs a title message for game information change notifications.
     *
     * @param chatId the Telegram chat ID
     * @param locale the user's locale for localized messaging
     * @return a {@link SendMessage} object representing the title message
     */
    private SendMessage getGameInfoChangingTitle(final long chatId, final Locale locale) {
        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(messageSource.getMessage("game.change.info.notification", null, locale))
                .build();
    }

    /**
     * Maps a game information change event to a Telegram message.
     *
     * @param game the {@link GameInStoreDto} containing game details from the store
     * @param user the {@link TelegramUser} who will receive the notification
     * @return a {@link SendMessage} object containing the game change information
     */
    private SendMessage getGameChangeInfo(final GameInStoreDto game, final TelegramUser user) {
        return gameInStoreMapper.mapGameInStoreNotificationToTelegramPage(
                String.valueOf(user.getTelegramId()),
                game,
                user.getLocale()
        );
    }
}