package com.gpb.telegram.mapper;

import com.gpb.common.entity.game.GameInStoreDto;
import com.gpb.telegram.mapper.entity.TelegramButton;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Component
@AllArgsConstructor
public class GameInStoreMapper {

    private static final String GAME_INFO_FORM = "%s" + System.lineSeparator() + "%s" + System.lineSeparator() + "%s";
    private static final String GAME_NOTIFICATION_INFO_FORM = "%s" + System.lineSeparator() + "%s";
    private static final String GAME_PRICE_WITH_DISCOUNT_FORM = "<s>%s ₴</s> <code>-%s%%</code> %s ₴";

    private final TelegramKeyboardMapper telegramKeyboardMapper;
    private final GameMapper gameMapper;

    public SendMessage mapGameInStoreNotificationToTelegramPage(String chatId, GameInStoreDto game, Locale locale) {
        return getSendMessageBuilder(chatId, game, locale)
                .text(String.format(GAME_NOTIFICATION_INFO_FORM, game.getNameInStore(), getGameInfoForm(game, locale)))
                .build();
    }

    public SendMessage mapGamesInStoreToTelegramPage(String chatId, GameInStoreDto game, Locale locale) {
        return getSendMessageBuilder(chatId, game, locale)
                .text(getGameInfoForm(game, locale))
                .build();
    }

    private String getGameInfoForm(GameInStoreDto game, Locale locale) {
        return String.format(GAME_INFO_FORM, getHostnameFromUrl(game.getUrl()),
                gameMapper.getIsAvailableForm(game.isAvailable(), locale), getPrice(game));
    }

    private SendMessage.SendMessageBuilder getSendMessageBuilder(String chatId, GameInStoreDto game, Locale locale) {
        return SendMessage.builder()
                .chatId(chatId)
                .replyMarkup(getKeyboardForGame(game.getUrl(), locale))
                .parseMode(ParseMode.HTML);
    }

    private String getPrice(GameInStoreDto game) {
        return game.getDiscount() > 0
                ? String.format(GAME_PRICE_WITH_DISCOUNT_FORM, game.getPrice(), game.getDiscount(), game.getDiscountPrice())
                : game.getPrice().toString() + " ₴";
    }

    private InlineKeyboardMarkup getKeyboardForGame(String url, Locale locale) {
        List<List<TelegramButton>> settingList = Collections
                .singletonList(Collections.singletonList(
                        TelegramButton.builder()
                                .textCode("game.info.in.store")
                                .url(url)
                                .locale(locale).build()));

        return telegramKeyboardMapper.getKeyboardMarkup(settingList);
    }

    private static String getHostnameFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            return url.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "";
        }
    }
}
