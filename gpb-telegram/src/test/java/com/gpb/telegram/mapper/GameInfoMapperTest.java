package com.gpb.telegram.mapper;

import com.gpb.common.entity.game.GameInStoreDto;
import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.util.UpdateCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameInfoMapperTest {

    @Mock
    GameInStoreMapper gameInStoreMapper;
    @Mock
    GameMapper gameMapper;
    @InjectMocks
    GameInfoMapper gameInfoMapper;

    @Test
    void testMapGameInfoToTelegramPage_whenSuccess_shouldReturnListOfMessages() {
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

        SendPhoto gameCommonInfoMessage = new SendPhoto();
        Update update = UpdateCreator.getUpdateWithoutCallback("", Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).build();
        when(gameMapper.mapGameToPhotoMessage(request, gameInfoDto))
                .thenReturn(gameCommonInfoMessage);
        SendMessage gameInStoreMessage = new SendMessage();
        when(gameInStoreMapper.mapGamesInStoreToTelegramPage(chatId, gameInShops.get(0), request.getLocale())).thenReturn(gameInStoreMessage);


        List<PartialBotApiMethod> partialBotApiMethodList = gameInfoMapper.mapGameInfoToTelegramPage(gameInfoDto, request);


        assertEquals(2, partialBotApiMethodList.size());
        assertEquals(gameCommonInfoMessage, partialBotApiMethodList.get(0));
        SendMessage message = (SendMessage) partialBotApiMethodList.get(1);
        assertEquals(message, gameInStoreMessage);
    }
}