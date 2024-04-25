package com.gpb.telegram.handler;

import com.gpb.telegram.controller.TelegramController;
import com.gpb.telegram.exception.NotExistingMessengerActivationTokenException;
import com.gpb.telegram.filter.FilterChain;
import com.gpb.telegram.util.Consts;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ControllerHandlerTest {

    ExceptionHandler exceptionHandler = mock(ExceptionHandler.class);
    TelegramController controller = mock(TelegramController.class);
    Map<String, TelegramController> controllers = new HashMap<>();
    FilterChain filterChain = mock(FilterChain.class);

    ControllerHandler controllerHandler;

    ControllerHandlerTest() {
        controllers.put("command", controller);
        controllerHandler = new ControllerHandler(exceptionHandler, controllers, filterChain);
    }

    @Test
    void testHandleCommands_whenExistingCommand_shouldReturnResult() {
        String chatId = "123";
        Update update = getUpdate("command", Long.parseLong(chatId));

        String response = "message";
        when(controller.apply(chatId, update))
                .thenReturn(new SendMessage(chatId, response));


        SendMessage result = controllerHandler.handleCommands(update);


        verify(filterChain, times(1)).handleFilterChain(controller, update);
        assertEquals(chatId, result.getChatId());
        assertEquals(response, result.getText());
    }

    @Test
    void testHandleCommands_whenNotExistingCommand_shouldReturnUnknownCommandMessage() {
        String chatId = "123";
        Update update = getUpdate("notExistingCommand", Long.parseLong(chatId));


        SendMessage result = controllerHandler.handleCommands(update);


        verify(filterChain, times(0)).handleFilterChain(controller, update);
        assertEquals(chatId, result.getChatId());
        assertEquals(Consts.UNKNOWN_COMMAND, result.getText());
    }

    @Test
    void testHandleCommands_whenFilterChainThrowException_shouldReturnExceptionMessage() {
        String chatId = "123";
        Update update = getUpdate("command", Long.parseLong(chatId));
        RuntimeException exception = new NotExistingMessengerActivationTokenException();
        String exceptionResponse = exception.getMessage();

        doThrow(exception).when(filterChain).handleFilterChain(controller, update);
        when(exceptionHandler.handleException(chatId, exception))
                .thenReturn(new SendMessage(chatId, exceptionResponse));


        SendMessage result = controllerHandler.handleCommands(update);


        assertEquals(chatId, result.getChatId());
        assertEquals(new NotExistingMessengerActivationTokenException().getMessage(), result.getText());
    }

    @Test
    void testHandleCommands_whenCommandThrowException_shouldReturnExceptionMessage() {
        String chatId = "123";
        Update update = getUpdate("command", Long.parseLong(chatId));
        RuntimeException exception = new NotExistingMessengerActivationTokenException();
        String exceptionResponse = exception.getMessage();

        when(controller.apply(chatId, update))
                .thenThrow(exception);
        when(exceptionHandler.handleException(chatId, exception))
                .thenReturn(new SendMessage(chatId, exceptionResponse));


        SendMessage result = controllerHandler.handleCommands(update);


        verify(filterChain, times(1)).handleFilterChain(controller, update);
        assertEquals(chatId, result.getChatId());
        assertEquals(new NotExistingMessengerActivationTokenException().getMessage(), result.getText());
    }

    private Update getUpdate(String text, long chatId) {
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();

        chat.setId(chatId);
        update.setMessage(message);
        message.setText(text);
        message.setChat(chat);
        return update;
    }
}