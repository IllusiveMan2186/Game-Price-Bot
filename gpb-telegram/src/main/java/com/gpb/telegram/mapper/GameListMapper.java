package com.gpb.telegram.mapper;

import com.gpb.telegram.bean.Game;
import com.gpb.telegram.bean.GameInShop;
import com.gpb.telegram.bean.Genre;
import com.gpb.telegram.configuration.ResourceConfiguration;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


@Component
@AllArgsConstructor
public class GameListMapper {

    private static final String GAME_INFO_FORM = "%s" + System.lineSeparator() + "%s" + System.lineSeparator()
            + "%s" + "%s - %s ₴";

    private MessageSource messageSource;
    private final TelegramKeyboardMapper telegramKeyboardMapper;
    private final ResourceConfiguration resourceConfiguration;


    public List<PartialBotApiMethod> gameSearchListToTelegramPage(List<Game> games, long gameAmount, String chatId, int pageNum,
                                                                  String gameName, Locale locale) {
        List<PartialBotApiMethod> messages = new ArrayList<>();
        games.forEach(game -> messages.add(getPhotoMessage(chatId, game, locale)));
        if (gameAmount / Constants.GAMES_AMOUNT_IN_LIST > pageNum) {
            messages.add(getNextPageButton(chatId, pageNum, gameName, locale));
        }
        return messages;
    }

    private SendPhoto getPhotoMessage(String chatId, Game game, Locale locale) {
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(getGameImage(game.getName()))
                .caption(mapGameToTelegramMessage(game, locale))
                .replyMarkup(getKeyboardForGame(game.getId(), locale))
                .build();
    }

    private InputFile getGameImage(String gameName) {
        String filePath = resourceConfiguration.getImageFolder() + "/" + gameName + Constants.JPG_IMG_FILE_EXTENSION;
        return new InputFile(new File(filePath));
    }

    private String mapGameToTelegramMessage(Game game, Locale locale) {
        BigDecimal minPrice = game.getGamesInShop().stream()
                .map(GameInShop::getDiscountPrice)
                .max(Comparator.naturalOrder())
                .orElse(null);
        BigDecimal maxPrice = game.getGamesInShop().stream()
                .map(GameInShop::getDiscountPrice)
                .min(Comparator.naturalOrder())
                .orElse(null);
        boolean isAvailable = game.getGamesInShop().stream().anyMatch(GameInShop::isAvailable);
        return String.format(GAME_INFO_FORM, game.getName(), getIsAvailableForm(isAvailable, locale),
                getGenreForm(game.getGenres(), locale), minPrice, maxPrice);
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

    private InlineKeyboardMarkup getKeyboardForGame(long gameId, Locale locale) {
        List<List<TelegramButton>> settingList = Collections
                .singletonList(Collections.singletonList(
                        TelegramButton.builder()
                                .textCode("game.search.list.next.page.more.game.info.button")
                                .callBackData(String.format("/gameInfo %s", gameId))
                                .locale(locale).build()));

        return telegramKeyboardMapper.getKeyboardMarkup(settingList);
    }


}
