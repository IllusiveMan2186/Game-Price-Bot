package com.gpb.telegram.handler;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionHandlerTest {

    ExceptionHandler exceptionHandler = new ExceptionHandler();

    @Test
    void testHandleException_shouldReturnSendMessage() {
        String chatId = "chatId";
        String message = "message";
        RuntimeException exception = new RuntimeException(message);


        SendMessage result = exceptionHandler.handleException(chatId, exception);


        assertEquals("chatId", result.getChatId());
        assertEquals(message, result.getText());
    }
}