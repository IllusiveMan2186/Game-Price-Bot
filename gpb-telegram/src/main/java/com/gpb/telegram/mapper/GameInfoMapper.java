package com.gpb.telegram.mapper;

import com.gpb.telegram.bean.Game;
import com.gpb.telegram.bean.GameInShop;
import com.gpb.telegram.bean.TelegramRequest;
import com.gpb.telegram.bean.TelegramUser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


@Component
@AllArgsConstructor
public class GameInfoMapper {

    private static final String GAME_INFO_FORM = "%s" + System.lineSeparator() + "%s" + System.lineSeparator() + "%s";
    private static final String GAME_PRICE_WITH_DISCOUNT_FORM = "<s>%s ₴</s> <code>-%s%%</code> %s ₴";

    private final TelegramKeyboardMapper telegramKeyboardMapper;
    private final GameListMapper gameListMapper;


    public List<PartialBotApiMethod> gameInfoToTelegramPage(Game game, TelegramRequest request) {
        List<PartialBotApiMethod> messages = gameListMapper
                .gameSearchListToTelegramPage(Collections.singletonList(game),request , 1, 1, game.getName());
        messages.addAll(game.getGamesInShop()
                .stream()
                .map(gameInShop -> getGamesInShop(request.getChatId(), gameInShop, request.getLocale()))
                .toList());
        return messages;
    }

    private SendMessage getGamesInShop(String chatId, GameInShop game, Locale locale) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(String.format(GAME_INFO_FORM, getHostnameFromUrl(game.getUrl()),
                        gameListMapper.getIsAvailableForm(game.isAvailable(), locale), getPrice(game)))
                .replyMarkup(getKeyboardForGame(game.getUrl(), locale))
                .parseMode(ParseMode.HTML)
                .build();
    }

    private String getPrice(GameInShop game) {
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

    public static String getHostnameFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            return url.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "";
        }
    }
}
