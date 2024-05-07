package com.gpb.telegram.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TelegramKeyboardMapperTest {
    MessageSource messageSource = mock(MessageSource.class);

    TelegramKeyboardMapper telegramKeyboardMapper = new TelegramKeyboardMapper(messageSource);

    @Test
    void testGetKeyboardMarkup_whenWithCallback_shouldReturnListWithCallbackButton() {
        Locale locale = new Locale("");
        TelegramButton telegramButton = TelegramButton.builder()
                .textCode("code")
                .callBackData("/command")
                .locale(locale).build();
        when(messageSource.getMessage("code", null, locale)).thenReturn("text");
        List<List<TelegramButton>> linesSetting = Collections.singletonList(Collections.singletonList(telegramButton));


        InlineKeyboardMarkup inlineKeyboardMarkup = telegramKeyboardMapper.getKeyboardMarkup(linesSetting);


        assertEquals("InlineKeyboardMarkup(keyboard=[[InlineKeyboardButton(text=text, url=null, " +
                        "callbackData=/command, callbackGame=null, switchInlineQuery=null, " +
                        "switchInlineQueryCurrentChat=null, pay=null, loginUrl=null, webApp=null)]])",
                inlineKeyboardMarkup.toString());
    }

    @Test
    void testGetKeyboardMarkup_whenWithUrl_shouldReturnListWithUrlButton() {
        Locale locale = new Locale("");
        TelegramButton telegramButton = TelegramButton.builder()
                .textCode("code")
                .url("http://localhost:3000")
                .locale(locale).build();
        when(messageSource.getMessage("code", null, locale)).thenReturn("text");
        List<List<TelegramButton>> linesSetting = Collections.singletonList(Collections.singletonList(telegramButton));


        InlineKeyboardMarkup inlineKeyboardMarkup = telegramKeyboardMapper.getKeyboardMarkup(linesSetting);


        assertEquals("InlineKeyboardMarkup(keyboard=[[InlineKeyboardButton(text=text, " +
                        "url=http://localhost:3000, callbackData=null, callbackGame=null, switchInlineQuery=null, " +
                        "switchInlineQueryCurrentChat=null, pay=null, loginUrl=null, webApp=null)]])",
                inlineKeyboardMarkup.toString());
    }
}