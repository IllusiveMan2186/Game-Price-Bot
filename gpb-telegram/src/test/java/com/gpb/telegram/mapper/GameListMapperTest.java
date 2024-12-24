package com.gpb.telegram.mapper;

import com.gpb.common.entity.game.GameDto;
import com.gpb.common.entity.game.Genre;
import com.gpb.telegram.configuration.ResourceConfiguration;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramUser;
import com.gpb.telegram.util.UpdateCreator;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameListMapperTest {

    MessageSource messageSource = mock(MessageSource.class);
    ResourceConfiguration configuration = mock(ResourceConfiguration.class);
    GameListMapper gameListMapper = new GameListMapper(messageSource, new TelegramKeyboardMapper(messageSource), configuration);

    @Test
    void testGameListToTelegramPage_whenHasNextPage_shouldReturnMessagesListWithNextPageButton() {
        List<GameDto> games = new ArrayList<>();
        games.add(GameDto.builder()
                .name("name1")
                .maxPrice(new BigDecimal(200))
                .minPrice(new BigDecimal(200))
                .genres(new ArrayList<>()).build());
        long gameAmount = 12;
        int pageNum = 1;
        String chatId = "123";
        String gameName = "name";
        Locale locale = new Locale("");
        Update update = UpdateCreator.getUpdateWithoutCallback("", Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).build();
        when(messageSource.getMessage("game.search.list.next.page.more.button", null, locale)).thenReturn("button");
        when(messageSource.getMessage("game.search.list.next.page.text", null, locale)).thenReturn("text");
        when(messageSource.getMessage("game.info.available", null, locale)).thenReturn("available");
        when(messageSource.getMessage("game.more.info.button", null, locale)).thenReturn("info");
        when(messageSource.getMessage("game.info.genre", null, locale)).thenReturn("genre");
        when(messageSource.getMessage("game.subscribe.button", null, locale)).thenReturn("subscribe.button");


        List<PartialBotApiMethod> partialBotApiMethods = gameListMapper
                .gameSearchListToTelegramPage(games, request, gameAmount, pageNum, gameName);


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
        List<GameDto> games = new ArrayList<>();
        games.add(GameDto.builder()
                .name("name1")
                .isAvailable(true)
                .maxPrice(new BigDecimal(200))
                .minPrice(new BigDecimal(200))
                .genres(List.of(Genre.ONLINE)).build());
        long gameAmount = 4;
        int pageNum = 2;
        String chatId = "123";
        String gameName = "name";
        Locale locale = new Locale("");
        Update update = UpdateCreator.getUpdateWithoutCallback("", Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).build();
        when(messageSource.getMessage("game.search.list.next.page.more.button", null, locale)).thenReturn("button");
        when(messageSource.getMessage("game.search.list.next.page.text", null, locale)).thenReturn("text");
        when(messageSource.getMessage("game.info.available", null, locale)).thenReturn("available");
        when(messageSource.getMessage("game.more.info.button", null, locale)).thenReturn("info");
        when(messageSource.getMessage("game.info.genre", null, locale)).thenReturn("genre");
        when(messageSource.getMessage("game.info.genre.online", null, locale)).thenReturn("online");
        when(messageSource.getMessage("game.info.genre.action", null, locale)).thenReturn("action");
        when(messageSource.getMessage("game.subscribe.button", null, locale)).thenReturn("subscribe.button");


        List<PartialBotApiMethod> partialBotApiMethods = gameListMapper
                .gameSearchListToTelegramPage(games, request, gameAmount, pageNum, gameName);


        assertEquals(1, partialBotApiMethods.size());
        SendPhoto photo = (SendPhoto) partialBotApiMethods.get(0);
        assertEquals(chatId, photo.getChatId());
        assertEquals(games.get(0).getName() + System.lineSeparator() + "available" + System.lineSeparator()
                + "genre : online" + System.lineSeparator() + "200 - 200 ₴", photo.getCaption());
        assertEquals("name1.jpg", photo.getPhoto().getMediaName());
    }

    @Test
    void testGameListToTelegramPage_whenGameHaveGenre_shouldReturnMessagesListWithGenreList() {
        List<GameDto> games = new ArrayList<>();
        List<Genre> genres = new ArrayList<>();
        genres.add(Genre.ONLINE);
        genres.add(Genre.ACTION);
        games.add(GameDto.builder()
                .name("name1")
                .isAvailable(true)
                .maxPrice(new BigDecimal(200))
                .minPrice(new BigDecimal(200))
                .genres(genres)
                .build());
        long gameAmount = 2;
        int pageNum = 1;
        String chatId = "123";
        String gameName = "name";
        Locale locale = new Locale("");
        Update update = UpdateCreator.getUpdateWithoutCallback("", Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).build();
        when(messageSource.getMessage("game.search.list.next.page.more.button", null, locale)).thenReturn("button");
        when(messageSource.getMessage("game.search.list.next.page.text", null, locale)).thenReturn("text");
        when(messageSource.getMessage("game.info.available", null, locale)).thenReturn("available");
        when(messageSource.getMessage("game.more.info.button", null, locale)).thenReturn("info");
        when(messageSource.getMessage("game.info.genre", null, locale)).thenReturn("genre");
        when(messageSource.getMessage("game.info.genre.online", null, locale)).thenReturn("online");
        when(messageSource.getMessage("game.info.genre.action", null, locale)).thenReturn("action");
        when(messageSource.getMessage("game.subscribe.button", null, locale)).thenReturn("subscribe.button");


        List<PartialBotApiMethod> partialBotApiMethods = gameListMapper
                .gameSearchListToTelegramPage(games, request, gameAmount, pageNum, gameName);


        assertEquals(1, partialBotApiMethods.size());
        SendPhoto photo = (SendPhoto) partialBotApiMethods.get(0);
        assertEquals(chatId, photo.getChatId());
        assertEquals(games.get(0).getName() + System.lineSeparator() + "available" + System.lineSeparator()
                + "genre : online, action" + System.lineSeparator() + "200 - 200 ₴", photo.getCaption());
        assertEquals("name1.jpg", photo.getPhoto().getMediaName());
    }

    @Test
    void testGameListToTelegramPage_whenUserNotRegistered_shouldReturnMessagesListWithSubscribeButton() {
        List<GameDto> games = new ArrayList<>();
        games.add(GameDto.builder()
                .name("name1")
                .isAvailable(true)
                .maxPrice(new BigDecimal(200))
                .minPrice(new BigDecimal(200))
                .genres(List.of(Genre.ONLINE))
                .isUserSubscribed(true).build());
        long gameAmount = 2;
        int pageNum = 1;
        String chatId = "123";
        String gameName = "name";
        Locale locale = new Locale("");
        Update update = UpdateCreator.getUpdateWithoutCallback("", Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).build();
        when(messageSource.getMessage("game.search.list.next.page.more.button", null, locale)).thenReturn("button");
        when(messageSource.getMessage("game.search.list.next.page.text", null, locale)).thenReturn("text");
        when(messageSource.getMessage("game.info.available", null, locale)).thenReturn("available");
        when(messageSource.getMessage("game.more.info.button", null, locale)).thenReturn("info");
        when(messageSource.getMessage("game.info.genre", null, locale)).thenReturn("genre");
        when(messageSource.getMessage("game.info.genre.online", null, locale)).thenReturn("online");
        when(messageSource.getMessage("game.subscribe.button", null, locale)).thenReturn("subscribe.button");


        List<PartialBotApiMethod> partialBotApiMethods = gameListMapper
                .gameSearchListToTelegramPage(games, request, gameAmount, pageNum, gameName);


        assertEquals(1, partialBotApiMethods.size());
        SendPhoto photo = (SendPhoto) partialBotApiMethods.get(0);
        assertEquals(chatId, photo.getChatId());
        assertEquals("InlineKeyboardMarkup(keyboard=[[InlineKeyboardButton(text=info, url=null, " +
                "callbackData=/gameInfo 0, callbackGame=null, switchInlineQuery=null, " +
                "switchInlineQueryCurrentChat=null, pay=null, loginUrl=null, webApp=null)], " +
                "[InlineKeyboardButton(text=subscribe.button, url=null, callbackData=/subscribe 0, " +
                "callbackGame=null, switchInlineQuery=null, switchInlineQueryCurrentChat=null, pay=null, " +
                "loginUrl=null, webApp=null)]])", photo.getReplyMarkup().toString());
    }

    @Test
    void testGameListToTelegramPage_whenUserSubscribedToGame_shouldReturnMessagesListWithSubscribeButton() {
        List<GameDto> games = new ArrayList<>();
        games.add(GameDto.builder()
                .name("name1")
                .isAvailable(true)
                .maxPrice(new BigDecimal(200))
                .minPrice(new BigDecimal(200))
                .genres(List.of(Genre.ONLINE))
                .isUserSubscribed(true).build());
        long gameAmount = 2;
        int pageNum = 1;
        String chatId = "123";
        String gameName = "name";
        Locale locale = new Locale("");
        TelegramUser user = TelegramUser.builder().basicUserId(1).build();
        Update update = UpdateCreator.getUpdateWithoutCallback("", Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).user(user).build();
        when(messageSource.getMessage("game.search.list.next.page.more.button", null, locale)).thenReturn("button");
        when(messageSource.getMessage("game.search.list.next.page.text", null, locale)).thenReturn("text");
        when(messageSource.getMessage("game.info.available", null, locale)).thenReturn("available");
        when(messageSource.getMessage("game.more.info.button", null, locale)).thenReturn("info");
        when(messageSource.getMessage("game.info.genre", null, locale)).thenReturn("genre");
        when(messageSource.getMessage("game.info.genre.online", null, locale)).thenReturn("online");
        when(messageSource.getMessage("game.info.genre.action", null, locale)).thenReturn("action");
        when(messageSource.getMessage("game.unsubscribe.button", null, locale)).thenReturn("unsubscribe.button");


        List<PartialBotApiMethod> partialBotApiMethods = gameListMapper
                .gameSearchListToTelegramPage(games, request, gameAmount, pageNum, gameName);


        assertEquals(1, partialBotApiMethods.size());
        SendPhoto photo = (SendPhoto) partialBotApiMethods.get(0);
        assertEquals(chatId, photo.getChatId());
        assertEquals("InlineKeyboardMarkup(keyboard=[[InlineKeyboardButton(text=info, url=null, " +
                "callbackData=/gameInfo 0, callbackGame=null, switchInlineQuery=null, " +
                "switchInlineQueryCurrentChat=null, pay=null, loginUrl=null, webApp=null)], " +
                "[InlineKeyboardButton(text=unsubscribe.button, url=null, callbackData=/unsubscribe 0, " +
                "callbackGame=null, switchInlineQuery=null, switchInlineQueryCurrentChat=null, pay=null, " +
                "loginUrl=null, webApp=null)]])", photo.getReplyMarkup().toString());
    }
}