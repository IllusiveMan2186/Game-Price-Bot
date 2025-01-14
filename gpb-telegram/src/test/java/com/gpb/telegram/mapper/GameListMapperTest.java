package com.gpb.telegram.mapper;

import com.gpb.common.entity.game.GameDto;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.util.UpdateCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameListMapperTest {

    @Mock
    GameMapper gameMapper;
    @Mock
    ButtonFactory buttonFactory;
    @InjectMocks
    GameListMapper gameListMapper;

    @Test
    void testGameListToTelegramPage_whenHasNextPage_shouldReturnMessagesListWithNextPageButton() {
        List<GameDto> games = new ArrayList<>();
        games.add(GameDto.builder().build());
        long gameAmount = 12;
        int pageNum = 1;
        String chatId = "123";
        String gameName = "name";
        Locale locale = new Locale("");
        Update update = UpdateCreator.getUpdateWithoutCallback("", Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).build();
        SendPhoto gameTelegramPage = new SendPhoto();
        when(gameMapper.getGamePhotoMessage(request, games.get(0))).thenReturn(gameTelegramPage);


        List<PartialBotApiMethod> partialBotApiMethods = gameListMapper
                .gameSearchListToTelegramPage(games, request, gameAmount, pageNum, gameName);


        assertEquals(2, partialBotApiMethods.size());
        SendPhoto photo = (SendPhoto) partialBotApiMethods.get(0);
        assertEquals(gameTelegramPage, photo);
        verify(gameMapper).getGamePhotoMessage(request, games.get(0));
    }

    @Test
    void testGameListToTelegramPage_whenHasNotNextPage_shouldReturnMessagesListWithoutNextPageButton() {
        List<GameDto> games = new ArrayList<>();
        games.add(GameDto.builder().build());
        long gameAmount = 4;
        int pageNum = 2;
        String chatId = "123";
        String gameName = "name";
        Locale locale = new Locale("");
        Update update = UpdateCreator.getUpdateWithoutCallback("", Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).build();
        SendPhoto gameTelegramPage = new SendPhoto();
        when(gameMapper.getGamePhotoMessage(request, games.get(0))).thenReturn(gameTelegramPage);


        List<PartialBotApiMethod> partialBotApiMethods = gameListMapper
                .gameSearchListToTelegramPage(games, request, gameAmount, pageNum, gameName);


        assertEquals(1, partialBotApiMethods.size());
        SendPhoto photo = (SendPhoto) partialBotApiMethods.get(0);
        assertEquals(gameTelegramPage, photo);
        verify(gameMapper).getGamePhotoMessage(request, games.get(0));
    }

    @Test
    void testUserGameListToTelegramPage_whenHasNextPage_shouldReturnMessagesListWithNextPageButton() {
        List<GameDto> games = new ArrayList<>();
        games.add(GameDto.builder().build());
        long gameAmount = 12;
        int pageNum = 1;
        String chatId = "123";
        Locale locale = new Locale("");
        Update update = UpdateCreator.getUpdateWithoutCallback("", Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).build();
        SendPhoto gameTelegramPage = new SendPhoto();
        when(gameMapper.getGamePhotoMessage(request, games.get(0))).thenReturn(gameTelegramPage);


        List<PartialBotApiMethod> partialBotApiMethods = gameListMapper
                .userGameListToTelegramPage(games, request, gameAmount, pageNum);


        assertEquals(2, partialBotApiMethods.size());
        SendPhoto photo = (SendPhoto) partialBotApiMethods.get(0);
        assertEquals(gameTelegramPage, photo);
        verify(gameMapper).getGamePhotoMessage(request, games.get(0));
    }

    @Test
    void testUerGameListToTelegramPage_whenHasNotNextPage_shouldReturnMessagesListWithoutNextPageButton() {
        List<GameDto> games = new ArrayList<>();
        games.add(GameDto.builder().build());
        long gameAmount = 4;
        int pageNum = 2;
        String chatId = "123";
        Locale locale = new Locale("");
        Update update = UpdateCreator.getUpdateWithoutCallback("", Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).build();
        SendPhoto gameTelegramPage = new SendPhoto();
        when(gameMapper.getGamePhotoMessage(request, games.get(0))).thenReturn(gameTelegramPage);


        List<PartialBotApiMethod> partialBotApiMethods = gameListMapper
                .userGameListToTelegramPage(games, request, gameAmount, pageNum);


        assertEquals(1, partialBotApiMethods.size());
        SendPhoto photo = (SendPhoto) partialBotApiMethods.get(0);
        assertEquals(gameTelegramPage, photo);
        verify(gameMapper).getGamePhotoMessage(request, games.get(0));
    }
}