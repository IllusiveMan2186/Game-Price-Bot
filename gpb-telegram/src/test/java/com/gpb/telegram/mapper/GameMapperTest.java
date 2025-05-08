package com.gpb.telegram.mapper;


import com.gpb.common.entity.game.GameDto;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.util.CommonConstants;
import com.gpb.telegram.configuration.ResourceConfiguration;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.service.GameService;
import com.gpb.telegram.util.Constants;
import com.gpb.telegram.util.UpdateCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameMapperTest {

    @Mock
    MessageSource messageSource;
    @Mock
    TelegramKeyboardMapper telegramKeyboardMapper;
    @Mock
    ResourceConfiguration configuration;
    @Mock
    ButtonFactory buttonFactory;
    @Mock
    GameService gameService;
    @InjectMocks
    GameMapper gameMapper;


    @Test
    void testMapameListToTelegramPage_whenGameHaveGenre_shouldReturnMessagesListWithGenreList() throws IOException {
        List<Genre> genres = new ArrayList<>();
        genres.add(Genre.ONLINE);
        genres.add(Genre.ACTION);
        GameDto game = GameDto.builder()
                .name("name1")
                .isAvailable(true)
                .maxPrice(new BigDecimal(200))
                .minPrice(new BigDecimal(200))
                .genres(genres)
                .build();
        String chatId = "123";
        byte [] image = "name1.jpg".getBytes();
        Locale locale = new Locale("");
        Update update = UpdateCreator.getUpdateWithoutCallback("", Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).build();
        when(messageSource.getMessage("game.info.available", null, locale)).thenReturn("available");
        when(messageSource.getMessage("game.info.genre", null, locale)).thenReturn("genre");
        when(messageSource.getMessage("game.info.genre.online", null, locale)).thenReturn("online");
        when(messageSource.getMessage("game.info.genre.action", null, locale)).thenReturn("action");
        when(gameService.getGameImage(game.getName())).thenReturn(image);


        SendPhoto photo = gameMapper
                .mapGameToPhotoMessage(request, game);


        assertEquals(chatId, photo.getChatId());
        assertEquals(game.getName() + CommonConstants.JPG_IMG_FILE_EXTENSION, photo.getPhoto().getMediaName());
        assertEquals(game.getName() + System.lineSeparator() + "available" + System.lineSeparator()
                + "genre : online, action" + System.lineSeparator() + "200 - 200 ₴", photo.getCaption());
    }
}