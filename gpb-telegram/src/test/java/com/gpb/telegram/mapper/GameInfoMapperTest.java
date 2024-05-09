package com.gpb.telegram.mapper;

import com.gpb.telegram.bean.Game;
import com.gpb.telegram.bean.GameInShop;
import com.gpb.telegram.bean.TelegramUser;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameInfoMapperTest {

    TelegramKeyboardMapper telegramKeyboardMapper = mock(TelegramKeyboardMapper.class);
    GameListMapper gameListMapper = mock(GameListMapper.class);

    GameInfoMapper gameInfoMapper = new GameInfoMapper(telegramKeyboardMapper, gameListMapper);

    @Test
    void textGameInfoToTelegramPage_shouldReturnListOfMessages() {
        String chatId = "123";
        Locale locale = new Locale("en");
        String url = "http://localhost:3000/some/url";
        Set<GameInShop> gameInShops = new HashSet<>();
        gameInShops.add(GameInShop.builder()
                .price(new BigDecimal(500))
                .discount(50)
                .discountPrice(new BigDecimal(250))
                .url(url)
                .isAvailable(true).build());
        Game game = Game.builder().name("name1").gamesInShop(gameInShops).genres(new ArrayList<>()).build();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        SendMessage gameCommonInfoMessage = new SendMessage();
        TelegramUser user = new TelegramUser();
        List<PartialBotApiMethod> gameCommonInfoMessageList = new ArrayList<>();
        gameCommonInfoMessageList.add(gameCommonInfoMessage);
        when(gameListMapper.gameSearchListToTelegramPage(Collections.singletonList(game), user, 1,
                chatId, 1, game.getName(), locale))
                .thenReturn(gameCommonInfoMessageList);
        when(gameListMapper.getIsAvailableForm(true, locale)).thenReturn("available");
        List<List<TelegramButton>> settingList = Collections
                .singletonList(Collections.singletonList(
                        TelegramButton.builder()
                                .textCode("game.info.in.store")
                                .url(url)
                                .locale(locale).build()));
        when(telegramKeyboardMapper.getKeyboardMarkup(settingList)).thenReturn(inlineKeyboardMarkup);


        List<PartialBotApiMethod> partialBotApiMethodList = gameInfoMapper.gameInfoToTelegramPage(game, user, chatId, locale);


        assertEquals(2, partialBotApiMethodList.size());
        assertEquals(gameCommonInfoMessage, partialBotApiMethodList.get(0));
        SendMessage message = (SendMessage) partialBotApiMethodList.get(1);
        assertEquals(chatId, message.getChatId());
        assertEquals("localhost" + System.lineSeparator() + "available" + System.lineSeparator()
                + "<s>500 ₴</s> <code>-50%</code> 250 ₴", message.getText());
        assertEquals(inlineKeyboardMarkup, message.getReplyMarkup());
    }

    @Test
    void textGameInfoToTelegramPage_whenNotDiscount_shouldReturnListOfMessagesWithoutDiscount() {
        String chatId = "123";
        Locale locale = new Locale("en");
        String url = "http://localhost:3000/some/url";
        Set<GameInShop> gameInShops = new HashSet<>();
        gameInShops.add(GameInShop.builder()
                .price(new BigDecimal(500))
                .url(url)
                .isAvailable(true).build());
        Game game = Game.builder().name("name1").gamesInShop(gameInShops).genres(new ArrayList<>()).build();
        TelegramUser user = new TelegramUser();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        SendMessage gameCommonInfoMessage = new SendMessage();
        List<PartialBotApiMethod> gameCommonInfoMessageList = new ArrayList<>();
        gameCommonInfoMessageList.add(gameCommonInfoMessage);
        when(gameListMapper.gameSearchListToTelegramPage(Collections.singletonList(game), user, 1,
                chatId, 1, game.getName(), locale))
                .thenReturn(gameCommonInfoMessageList);
        when(gameListMapper.getIsAvailableForm(true, locale)).thenReturn("available");
        List<List<TelegramButton>> settingList = Collections
                .singletonList(Collections.singletonList(
                        TelegramButton.builder()
                                .textCode("game.info.in.store")
                                .url(url)
                                .locale(locale).build()));
        when(telegramKeyboardMapper.getKeyboardMarkup(settingList)).thenReturn(inlineKeyboardMarkup);


        List<PartialBotApiMethod> partialBotApiMethodList = gameInfoMapper.gameInfoToTelegramPage(game, user, chatId, locale);


        assertEquals(2, partialBotApiMethodList.size());
        assertEquals(gameCommonInfoMessage, partialBotApiMethodList.get(0));
        SendMessage message = (SendMessage) partialBotApiMethodList.get(1);
        assertEquals(chatId, message.getChatId());
        assertEquals("localhost" + System.lineSeparator() + "available" + System.lineSeparator()
                + "500 ₴", message.getText());
        assertEquals(inlineKeyboardMarkup, message.getReplyMarkup());
    }
}