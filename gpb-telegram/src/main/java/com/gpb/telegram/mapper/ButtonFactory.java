package com.gpb.telegram.mapper;

import com.gpb.common.entity.game.GameDto;
import com.gpb.telegram.entity.TelegramUser;
import com.gpb.telegram.mapper.entity.TelegramButton;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Component
@AllArgsConstructor
public class ButtonFactory {

    private final MessageSource messageSource;
    private final TelegramKeyboardMapper telegramKeyboardMapper;

    public SendMessage getNextPageButtonForSearchByName(String chatId, int pageNum, String gameName, Locale locale) {
        String callbackData = String.format("/searchByName %s %s", pageNum + 1, gameName);

        return getNextPageButton(chatId, callbackData, locale);
    }

    public SendMessage getNextPageButtonForUserListOfGame(String chatId, int pageNum, Locale locale) {
        String callbackData = String.format("/userGameListCallback %s", pageNum + 1);

        return getNextPageButton(chatId, callbackData, locale);
    }

    public SendMessage getNextPageButtonForListOfGame(String chatId, int pageNum, Locale locale, String sort) {
        String callbackData = String.format("/gameListCallback %s %s", pageNum + 1, sort);

        return getNextPageButton(chatId, callbackData, locale);
    }

    public TelegramButton getGameInfoButton(long gameId, Locale locale) {
        return TelegramButton.builder()
                .textCode("game.more.info.button")
                .callBackData(String.format("/gameInfo %s", gameId))
                .locale(locale).build();
    }

    public TelegramButton getSubscribeButton(TelegramUser telegramUser, GameDto game, Locale locale) {
        String textCodeForSubscribeSection = "game.subscribe.button";
        String callBackDataForSubscribeSection = String.format("/subscribe %s", game.getId());
        if (telegramUser != null && game.isUserSubscribed()) {
            textCodeForSubscribeSection = "game.unsubscribe.button";
            callBackDataForSubscribeSection = String.format("/unsubscribe %s", game.getId());
        }

        return TelegramButton.builder()
                .textCode(textCodeForSubscribeSection)
                .callBackData(callBackDataForSubscribeSection)
                .locale(locale).build();
    }

    private SendMessage getNextPageButton(String chatId, String callbackData, Locale locale) {
        List<List<TelegramButton>> settingList = Collections
                .singletonList(Collections.singletonList(
                        TelegramButton.builder()
                                .textCode("game.search.list.next.page.more.button")
                                .callBackData(callbackData)
                                .locale(locale).build()));

        InlineKeyboardMarkup inlineKeyboardMarkup = telegramKeyboardMapper.getKeyboardMarkup(settingList);

        return SendMessage.builder()
                .chatId(chatId)
                .text(messageSource.getMessage("game.search.list.next.page.text", null, locale))
                .replyMarkup(inlineKeyboardMarkup).build();
    }
}
