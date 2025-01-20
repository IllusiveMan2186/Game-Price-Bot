package com.gpb.telegram.mapper;

import com.gpb.common.entity.game.GameInStoreDto;
import com.gpb.telegram.mapper.entity.TelegramButton;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameInStoreMapperTest {

    @Mock
    TelegramKeyboardMapper telegramKeyboardMapper;
    @Mock
    GameMapper gameMapper;
    @InjectMocks
    GameInStoreMapper gameInStoreMapper;

    @Test
    void testMapGameInStoreNotificationToTelegramPage_whenSuccess_shouldReturnListOfMessages() {
        String chatId = "123";
        Locale locale = new Locale("en");
        String url = "http://localhost:3000/some/url";
        GameInStoreDto gameInStoreDto = GameInStoreDto.builder()
                .nameInStore("name")
                .price(new BigDecimal(500))
                .discount(50)
                .discountPrice(new BigDecimal(250))
                .url(url)
                .isAvailable(true).build();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        when(gameMapper.getIsAvailableForm(true, locale)).thenReturn("available");
        List<List<TelegramButton>> settingList = Collections
                .singletonList(Collections.singletonList(
                        TelegramButton.builder()
                                .textCode("game.info.in.store")
                                .url(url)
                                .locale(locale).build()));
        when(telegramKeyboardMapper.getKeyboardMarkup(settingList)).thenReturn(inlineKeyboardMarkup);


        SendMessage result = gameInStoreMapper.mapGameInStoreNotificationToTelegramPage(chatId, gameInStoreDto, locale);


        assertEquals(chatId, result.getChatId());
        assertEquals("name" + System.lineSeparator() + "localhost" + System.lineSeparator() + "available"
                + System.lineSeparator() + "<s>500 ₴</s> <code>-50%</code> 250 ₴", result.getText());
        assertEquals(inlineKeyboardMarkup, result.getReplyMarkup());
    }

    @Test
    void testMapGamesInStoreToTelegramPage_whenSuccess_shouldReturnListOfMessages() {
        String chatId = "123";
        Locale locale = new Locale("en");
        String url = "http://localhost:3000/some/url";
        GameInStoreDto gameInStoreDto = GameInStoreDto.builder()
                .price(new BigDecimal(500))
                .discount(50)
                .discountPrice(new BigDecimal(250))
                .url(url)
                .isAvailable(true).build();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        when(gameMapper.getIsAvailableForm(true, locale)).thenReturn("available");
        List<List<TelegramButton>> settingList = Collections
                .singletonList(Collections.singletonList(
                        TelegramButton.builder()
                                .textCode("game.info.in.store")
                                .url(url)
                                .locale(locale).build()));
        when(telegramKeyboardMapper.getKeyboardMarkup(settingList)).thenReturn(inlineKeyboardMarkup);


        SendMessage result = gameInStoreMapper.mapGamesInStoreToTelegramPage(chatId, gameInStoreDto, locale);


        assertEquals(chatId, result.getChatId());
        assertEquals("localhost" + System.lineSeparator() + "available" + System.lineSeparator()
                + "<s>500 ₴</s> <code>-50%</code> 250 ₴", result.getText());
        assertEquals(inlineKeyboardMarkup, result.getReplyMarkup());
    }

    @Test
    void testMapGamesInStoreToTelegramPage_whenNotDiscount_shouldReturnListOfMessagesWithoutDiscount() {
        String chatId = "123";
        Locale locale = new Locale("en");
        String url = "http://localhost:3000/some/url";
        GameInStoreDto gameInStoreDto = GameInStoreDto.builder()
                .price(new BigDecimal(500))
                .url(url)
                .isAvailable(true).build();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        when(gameMapper.getIsAvailableForm(true, locale)).thenReturn("available");
        List<List<TelegramButton>> settingList = Collections
                .singletonList(Collections.singletonList(
                        TelegramButton.builder()
                                .textCode("game.info.in.store")
                                .url(url)
                                .locale(locale).build()));
        when(telegramKeyboardMapper.getKeyboardMarkup(settingList)).thenReturn(inlineKeyboardMarkup);


        SendMessage result = gameInStoreMapper.mapGamesInStoreToTelegramPage(chatId, gameInStoreDto, locale);


        assertEquals(chatId, result.getChatId());
        assertEquals("localhost" + System.lineSeparator() + "available" + System.lineSeparator()
                + "500 ₴", result.getText());
        assertEquals(inlineKeyboardMarkup, result.getReplyMarkup());
    }
}