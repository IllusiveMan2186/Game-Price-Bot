package com.gpb.telegram.mapper;

import com.gpb.telegram.bean.Game;
import com.gpb.telegram.bean.GameInShop;
import com.gpb.telegram.bean.Genre;
import com.gpb.telegram.configuration.ResourceConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameListMapperTest {

    MessageSource messageSource = mock(MessageSource.class);
    ResourceConfiguration configuration = mock(ResourceConfiguration.class);
    GameListMapper gameListMapper = new GameListMapper(messageSource, new TelegramKeyboardMapper(messageSource), configuration);

    @Test
    void testGameListToTelegramPage_whenHasNextPage_shouldReturnMessagesListWithNextPageButton() {
        List<Game> games = new ArrayList<>();
        Set<GameInShop> gameInShops = new HashSet<>();
        gameInShops.add(GameInShop.builder().discountPrice(new BigDecimal(200)).build());
        games.add(Game.builder().name("name1").gamesInShop(gameInShops).genres(new ArrayList<>()).build());
        long gameAmount = 12;
        int pageNum = 1;
        String chatId = "";
        String gameName = "name";
        Locale locale = new Locale("");
        when(messageSource.getMessage("game.search.list.next.page.more.button", null, locale)).thenReturn("button");
        when(messageSource.getMessage("game.search.list.next.page.text", null, locale)).thenReturn("text");
        when(messageSource.getMessage("game.info.available", null, locale)).thenReturn("available");
        when(messageSource.getMessage("game.search.list.next.page.more.game.info.button", null, locale)).thenReturn("info");
        when(messageSource.getMessage("game.info.genre", null, locale)).thenReturn("genre");
        when(messageSource.getMessage("game.info.genre.online", null, locale)).thenReturn("online");


        List<PartialBotApiMethod> partialBotApiMethods = gameListMapper
                .gameSearchListToTelegramPage(games, gameAmount, chatId, pageNum, gameName, locale);


        assertEquals(2, partialBotApiMethods.size());
        SendPhoto photo = (SendPhoto) partialBotApiMethods.get(0);
        assertEquals(chatId, photo.getChatId());
        assertEquals("name1.jpg", photo.getPhoto().getMediaName());
        SendMessage message = (SendMessage) partialBotApiMethods.get(1);
        assertEquals(chatId, message.getChatId());
        assertEquals("text", message.getText());
        assertEquals("InlineKeyboardMarkup(keyboard=[[InlineKeyboardButton(text=button, url=null, " +
                        "callbackData=/searchByPage 2 name, callbackGame=null, switchInlineQuery=null, " +
                        "switchInlineQueryCurrentChat=null, pay=null, loginUrl=null, webApp=null)]])",
                message.getReplyMarkup().toString());
    }

    @Test
    void testGameListToTelegramPage_whenHasNotNextPage_shouldReturnMessagesListWithoutNextPageButton() {
        List<Game> games = new ArrayList<>();
        Set<GameInShop> gameInShops = new HashSet<>();
        gameInShops.add(GameInShop.builder().discountPrice(new BigDecimal(200)).isAvailable(true).build());
        List<Genre> genres = new ArrayList<>();
        genres.add(Genre.ONLINE);
        genres.add(Genre.ACTION);
        games.add(Game.builder().name("name1").gamesInShop(gameInShops).genres(genres).build());
        long gameAmount = 4;
        int pageNum = 2;
        String chatId = "";
        String gameName = "name";
        Locale locale = new Locale("");
        when(messageSource.getMessage("game.search.list.next.page.more.button", null, locale)).thenReturn("button");
        when(messageSource.getMessage("game.search.list.next.page.text", null, locale)).thenReturn("text");
        when(messageSource.getMessage("game.info.available", null, locale)).thenReturn("available");
        when(messageSource.getMessage("game.search.list.next.page.more.game.info.button", null, locale)).thenReturn("info");
        when(messageSource.getMessage("game.info.genre", null, locale)).thenReturn("genre");
        when(messageSource.getMessage("game.info.genre.online", null, locale)).thenReturn("online");
        when(messageSource.getMessage("game.info.genre.action", null, locale)).thenReturn("action");



        List<PartialBotApiMethod> partialBotApiMethods = gameListMapper
                .gameSearchListToTelegramPage(games, gameAmount, chatId, pageNum, gameName, locale);


        assertEquals(1, partialBotApiMethods.size());
        SendPhoto photo = (SendPhoto) partialBotApiMethods.get(0);
        assertEquals(chatId, photo.getChatId());
        assertEquals(games.get(0).getName() + System.lineSeparator() + "available" + System.lineSeparator()
                + "genre : online, action" + System.lineSeparator() + "200 - 200 ₴", photo.getCaption());
        assertEquals("name1.jpg", photo.getPhoto().getMediaName());
    }
}