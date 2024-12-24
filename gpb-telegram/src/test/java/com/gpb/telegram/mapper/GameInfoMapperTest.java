package com.gpb.telegram.mapper;

import com.gpb.common.entity.game.GameInStoreDto;
import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramUser;
import com.gpb.telegram.util.UpdateCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameInfoMapperTest {

    @Mock
    TelegramKeyboardMapper telegramKeyboardMapper;
    @Mock
    GameListMapper gameListMapper;
    @InjectMocks
    GameInfoMapper gameInfoMapper;

    @Test
    void testGameInfoToTelegramPage_whenSuccess_shouldReturnListOfMessages() {
        String chatId = "123";
        Locale locale = new Locale("en");
        String url = "http://localhost:3000/some/url";
        List<GameInStoreDto> gameInShops = new ArrayList<>();
        gameInShops.add(GameInStoreDto.builder()
                .price(new BigDecimal(500))
                .discount(50)
                .discountPrice(new BigDecimal(250))
                .url(url)
                .isAvailable(true).build());
        GameInfoDto gameInfoDto = GameInfoDto.builder()
                .name("name1")
                .gamesInShop(gameInShops)
                .genres(new ArrayList<>())
                .build();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        SendMessage gameCommonInfoMessage = new SendMessage();
        TelegramUser user = new TelegramUser();
        List<PartialBotApiMethod> gameCommonInfoMessageList = new ArrayList<>();
        gameCommonInfoMessageList.add(gameCommonInfoMessage);
        Update update = UpdateCreator.getUpdateWithoutCallback("", Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).build();
        when(gameListMapper.gameSearchListToTelegramPage(Collections.singletonList(gameInfoDto), request, 1
                , 1, gameInfoDto.getName()))
                .thenReturn(gameCommonInfoMessageList);
        when(gameListMapper.getIsAvailableForm(true, locale)).thenReturn("available");
        List<List<TelegramButton>> settingList = Collections
                .singletonList(Collections.singletonList(
                        TelegramButton.builder()
                                .textCode("game.info.in.store")
                                .url(url)
                                .locale(locale).build()));
        when(telegramKeyboardMapper.getKeyboardMarkup(settingList)).thenReturn(inlineKeyboardMarkup);


        List<PartialBotApiMethod> partialBotApiMethodList = gameInfoMapper.gameInfoToTelegramPage(gameInfoDto, request);


        assertEquals(2, partialBotApiMethodList.size());
        assertEquals(gameCommonInfoMessage, partialBotApiMethodList.get(0));
        SendMessage message = (SendMessage) partialBotApiMethodList.get(1);
        assertEquals(chatId, message.getChatId());
        assertEquals("localhost" + System.lineSeparator() + "available" + System.lineSeparator()
                + "<s>500 ₴</s> <code>-50%</code> 250 ₴", message.getText());
        assertEquals(inlineKeyboardMarkup, message.getReplyMarkup());
    }

    @Test
    void testGameInfoToTelegramPage_whenNotDiscount_shouldReturnListOfMessagesWithoutDiscount() {
        String chatId = "123";
        Locale locale = new Locale("en");
        String url = "http://localhost:3000/some/url";
        List<GameInStoreDto> gameInShops = new ArrayList<>();
        gameInShops.add(GameInStoreDto.builder()
                .price(new BigDecimal(500))
                .url(url)
                .isAvailable(true).build());
        GameInfoDto gameInfoDto = GameInfoDto.builder()
                .name("name1")
                .gamesInShop(gameInShops)
                .genres(new ArrayList<>())
                .build();
        TelegramUser user = new TelegramUser();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        SendMessage gameCommonInfoMessage = new SendMessage();
        List<PartialBotApiMethod> gameCommonInfoMessageList = new ArrayList<>();
        gameCommonInfoMessageList.add(gameCommonInfoMessage);
        Update update = UpdateCreator.getUpdateWithoutCallback("", Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).build();
        when(gameListMapper.gameSearchListToTelegramPage(Collections.singletonList(gameInfoDto), request, 1,
                1, gameInfoDto.getName()))
                .thenReturn(gameCommonInfoMessageList);
        when(gameListMapper.getIsAvailableForm(true, locale)).thenReturn("available");
        List<List<TelegramButton>> settingList = Collections
                .singletonList(Collections.singletonList(
                        TelegramButton.builder()
                                .textCode("game.info.in.store")
                                .url(url)
                                .locale(locale).build()));
        when(telegramKeyboardMapper.getKeyboardMarkup(settingList)).thenReturn(inlineKeyboardMarkup);


        List<PartialBotApiMethod> partialBotApiMethodList = gameInfoMapper.gameInfoToTelegramPage(gameInfoDto, request);


        assertEquals(2, partialBotApiMethodList.size());
        assertEquals(gameCommonInfoMessage, partialBotApiMethodList.get(0));
        SendMessage message = (SendMessage) partialBotApiMethodList.get(1);
        assertEquals(chatId, message.getChatId());
        assertEquals("localhost" + System.lineSeparator() + "available" + System.lineSeparator()
                + "500 ₴", message.getText());
        assertEquals(inlineKeyboardMarkup, message.getReplyMarkup());
    }
}