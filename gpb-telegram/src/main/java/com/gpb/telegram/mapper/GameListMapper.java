package com.gpb.telegram.mapper;

import com.gpb.common.entity.game.GameDto;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.util.CommonConstants;
import com.gpb.telegram.configuration.ResourceConfiguration;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramUser;
import com.gpb.telegram.util.Constants;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


@Component
@AllArgsConstructor
public class GameListMapper {

    private static final String GAME_INFO_FORM = "%s" + System.lineSeparator() + "%s" + System.lineSeparator()
            + "%s" + "%s - %s ₴";

    private final MessageSource messageSource;
    private final TelegramKeyboardMapper telegramKeyboardMapper;
    private final ResourceConfiguration resourceConfiguration;


    public List<PartialBotApiMethod> gameSearchListToTelegramPage(List<GameDto> games,
                                                                  TelegramRequest request,
                                                                  long gameAmount,
                                                                  int pageNum,
                                                                  String gameName) {
        List<PartialBotApiMethod> messages = new ArrayList<>();
        games.forEach(game -> messages.add(getPhotoMessage(request, game)));
        if (gameAmount / Constants.GAMES_AMOUNT_IN_LIST > pageNum) {
            messages.add(getNextPageButton(request.getChatId(), pageNum, gameName, request.getLocale()));
        }
        return messages;
    }

    private SendPhoto getPhotoMessage(TelegramRequest request, GameDto game) {
        return SendPhoto.builder()
                .chatId(request.getChatId())
                .photo(getGameImage(game.getName()))
                .caption(mapGameToTelegramMessage(game, request.getLocale()))
                .replyMarkup(getKeyboardForGame(request.getUser(), game, request.getLocale()))
                .build();
    }

    private InputFile getGameImage(String gameName) {
        String filePath = resourceConfiguration.getImageFolder() + "/" + gameName + CommonConstants.JPG_IMG_FILE_EXTENSION;
        return new InputFile(new File(filePath));
    }

    private String mapGameToTelegramMessage(GameDto game, Locale locale) {
        return String.format(GAME_INFO_FORM, game.getName(), getIsAvailableForm(game.isAvailable(), locale),
                getGenreForm(game.getGenres(), locale), game.getMinPrice(), game.getMaxPrice());
    }

    public String getIsAvailableForm(boolean isAvailable, Locale locale) {
        String textCode = isAvailable ? "game.info.available" : "game.info.not.available";
        return messageSource.getMessage(textCode, null, locale);
    }

    private String getGenreForm(List<Genre> genres, Locale locale) {
        if (!genres.isEmpty()) {
            StringBuilder genreForm = new StringBuilder(messageSource.getMessage("game.info.genre", null, locale));

            genreForm.append(" : ").append(getLocaleGenre(genres.get(0), locale));
            for (int i = 1; i < genres.size(); i++) {
                genreForm.append(", ").append(getLocaleGenre(genres.get(i), locale));
            }
            return genreForm.append(System.lineSeparator()).toString();
        }
        return "";
    }

    private String getLocaleGenre(Genre genre, Locale locale) {
        return messageSource.getMessage("game.info.genre." + genre.name().toLowerCase(), null, locale);
    }

    private SendMessage getNextPageButton(String chatId, int pageNum, String gameName, Locale locale) {
        String callbackData = String.format("/searchByPage %s %s", pageNum + 1, gameName);

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

    private InlineKeyboardMarkup getKeyboardForGame(TelegramUser telegramUser, GameDto game, Locale locale) {
        List<List<TelegramButton>> settingList = new ArrayList<>();
        settingList.add(Collections.singletonList(
                TelegramButton.builder()
                        .textCode("game.more.info.button")
                        .callBackData(String.format("/gameInfo %s", game.getId()))
                        .locale(locale).build()));

        String textCodeForSubscribeSection = "game.subscribe.button";
        String callBackDataForSubscribeSection = String.format("/subscribe %s", game.getId());
        if (telegramUser != null && game.isUserSubscribed()) {
            textCodeForSubscribeSection = "game.unsubscribe.button";
            callBackDataForSubscribeSection = String.format("/unsubscribe %s", game.getId());
        }

        settingList.add(Collections.singletonList(
                TelegramButton.builder()
                        .textCode(textCodeForSubscribeSection)
                        .callBackData(callBackDataForSubscribeSection)
                        .locale(locale).build()));

        return telegramKeyboardMapper.getKeyboardMarkup(settingList);
    }


}
