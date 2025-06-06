package com.gpb.telegram.handler;

import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.util.UpdateCreator;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionHandlerTest {

    ExceptionHandler exceptionHandler = new ExceptionHandler();

    @Test
    void testHandleException_whenSuccess_shouldReturnSendMessage() {
        String chatId = "123";
        String message = "messages";
        RuntimeException exception = new RuntimeException(message);
        Update update = UpdateCreator.getUpdateWithoutCallback("", Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).build();


        TelegramResponse result = exceptionHandler.handleException(request, exception);


        SendMessage sendMessage = (SendMessage) result.getMessages().get(0);
        assertEquals("123", sendMessage.getChatId());
        assertEquals(message, sendMessage.getText());
    }
}