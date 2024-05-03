package com.gpb.telegram.handler;

import com.gpb.telegram.bean.TelegramResponse;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionHandlerTest {

    ExceptionHandler exceptionHandler = new ExceptionHandler();

    @Test
    void testHandleException_shouldReturnSendMessage() {
        String chatId = "chatId";
        String message = "messages";
        RuntimeException exception = new RuntimeException(message);


        TelegramResponse result = exceptionHandler.handleException(chatId, exception);


        SendMessage sendMessage = (SendMessage) result.getMessages().get(0);
        assertEquals("chatId", sendMessage.getChatId());
        assertEquals(message, sendMessage.getText());
    }
}