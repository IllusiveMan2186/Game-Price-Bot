package com.gpb.telegram.mapper;

import com.gpb.telegram.bean.Game;
import com.gpb.telegram.bean.GameInShop;
import com.gpb.telegram.configuration.ResourceConfiguration;
import com.gpb.telegram.util.Constants;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


@Component
public class GameListMapper {

    private static final String GAME_INFO_FORM = "%s" + System.lineSeparator() + "%s - %s ₴";

    private final MessageSource messageSource;
    private final ResourceConfiguration resourceConfiguration;

    public GameListMapper(MessageSource messageSource, ResourceConfiguration resourceConfiguration) {
        this.messageSource = messageSource;
        this.resourceConfiguration = resourceConfiguration;
    }

    public List<PartialBotApiMethod> gameListToTelegramPage(List<Game> games, long gameAmount, String chatId, int pageNum,
                                                            String gameName, Locale locale) {
        List<PartialBotApiMethod> messages = new ArrayList<>();
        games.forEach(game -> messages.add(getPhotoMessage(chatId, game)));
        if (gameAmount / Constants.GAMES_AMOUNT_IN_LIST > pageNum) {
            messages.add(getNextPageButton(chatId, pageNum, gameName, locale));
        }
        return messages;
    }

    private SendPhoto getPhotoMessage(String chatId, Game game) {
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(getGameImage(game.getName()))
                .caption(mapGameToTelegramMessage(game)).build();
    }

    private InputFile getGameImage(String gameName) {
        String filePath = resourceConfiguration.getImageFolder() + "/" + gameName + Constants.JPG_IMG_FILE_EXTENSION;
        return new InputFile(new File(filePath));
    }

    private String mapGameToTelegramMessage(Game game) {
        BigDecimal minPrice = game.getGamesInShop().stream()
                .map(GameInShop::getDiscountPrice)
                .max(Comparator.naturalOrder())
                .orElse(null);
        BigDecimal maxPrice = game.getGamesInShop().stream()
                .map(GameInShop::getDiscountPrice)
                .min(Comparator.naturalOrder())
                .orElse(null);
        return String.format(GAME_INFO_FORM, game.getName(), minPrice, maxPrice);
    }

    private SendMessage getNextPageButton(String chatId, int pageNum, String gameName, Locale locale) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(messageSource.getMessage("game.search.list.next.page.more.button", null, locale));
        inlineKeyboardButton.setCallbackData(String.format("/searchByPage %s %s", pageNum + 1, gameName));

        List<List<InlineKeyboardButton>> rowList = Collections
                .singletonList(Collections.singletonList(inlineKeyboardButton));

        inlineKeyboardMarkup.setKeyboard(rowList);

        return SendMessage.builder()
                .chatId(chatId)
                .text(messageSource.getMessage("game.search.list.next.page.text", null, locale))
                .replyMarkup(inlineKeyboardMarkup).build();
    }
}
