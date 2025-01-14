package com.gpb.telegram.mapper;

import com.gpb.common.entity.game.GameDto;
import com.gpb.telegram.entity.TelegramUser;
import com.gpb.telegram.mapper.entity.TelegramButton;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ButtonFactoryTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private TelegramKeyboardMapper telegramKeyboardMapper;

    @InjectMocks
    private ButtonFactory buttonFactory;

    private Locale locale = Locale.ENGLISH;

    @Test
    void testGetNextPageButtonForSearchByName_whenSuccess_shouldReturnNextPageButton() {
        String chatId = "12345";
        int pageNum = 1;
        String gameName = "Chess";

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        when(telegramKeyboardMapper.getKeyboardMarkup(any())).thenReturn(keyboardMarkup);
        when(messageSource.getMessage("game.search.list.next.page.text", null, locale))
                .thenReturn("Next page");

        SendMessage result = buttonFactory.getNextPageButtonForSearchByName(chatId, pageNum, gameName, locale);

        assertNotNull(result);
        assertEquals(chatId, result.getChatId());
        assertEquals("Next page", result.getText());
        assertEquals(keyboardMarkup, result.getReplyMarkup());

        verify(telegramKeyboardMapper).getKeyboardMarkup(any());
        verify(messageSource).getMessage("game.search.list.next.page.text", null, locale);
    }

    @Test
    void testGetNextPageButtonForUserListOfGame_whenSuccess_shouldReturnNextPageButton() {
        String chatId = "12345";
        int pageNum = 1;
        String callbackData = "/userGameList 2";

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        when(telegramKeyboardMapper.getKeyboardMarkup(any())).thenReturn(keyboardMarkup);
        when(messageSource.getMessage("game.search.list.next.page.text", null, locale))
                .thenReturn("Next page");

        SendMessage result = buttonFactory.getNextPageButtonForUserListOfGame(chatId, pageNum, locale);

        assertNotNull(result);
        assertEquals(chatId, result.getChatId());
        assertEquals("Next page", result.getText());
        assertEquals(keyboardMarkup, result.getReplyMarkup());

        verify(telegramKeyboardMapper).getKeyboardMarkup(any());
        verify(messageSource).getMessage("game.search.list.next.page.text", null, locale);
    }

    @Test
    void testGetGameInfoButton_whenSuccess_shouldReturnMoreInfoButton() {
        long gameId = 123L;

        TelegramButton result = buttonFactory.getGameInfoButton(gameId, locale);

        assertNotNull(result);
        assertEquals("game.more.info.button", result.getTextCode());
        assertEquals("/gameInfo 123", result.getCallBackData());
        assertEquals(locale, result.getLocale());
    }

    @Test
    void testGetSubscribeButton_whenUserIsNotSubscribed_shouldReturnUnsubscribeButton() {
        TelegramUser telegramUser = null;
        GameDto game = new GameDto();
        game.setId(123L);
        game.setUserSubscribed(false);

        TelegramButton result = buttonFactory.getSubscribeButton(telegramUser, game, locale);

        assertNotNull(result);
        assertEquals("game.subscribe.button", result.getTextCode());
        assertEquals("/subscribe 123", result.getCallBackData());
        assertEquals(locale, result.getLocale());
    }

    @Test
    void testGetSubscribeButton_whenUserIsSubscribed_shouldReturnSubscribeButton() {
        TelegramUser telegramUser = new TelegramUser();
        GameDto game = new GameDto();
        game.setId(123L);
        game.setUserSubscribed(true);

        TelegramButton result = buttonFactory.getSubscribeButton(telegramUser, game, locale);

        assertNotNull(result);
        assertEquals("game.unsubscribe.button", result.getTextCode());
        assertEquals("/unsubscribe 123", result.getCallBackData());
        assertEquals(locale, result.getLocale());
    }
}
