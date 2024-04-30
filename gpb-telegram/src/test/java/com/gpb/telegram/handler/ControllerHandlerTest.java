package com.gpb.telegram.handler;

import com.gpb.telegram.controller.TelegramController;
import com.gpb.telegram.exception.NotExistingMessengerActivationTokenException;
import com.gpb.telegram.filter.FilterChain;
import com.gpb.telegram.service.TelegramUserService;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ControllerHandlerTest {

    MessageSource messageSource = mock(MessageSource.class);
    ExceptionHandler exceptionHandler = mock(ExceptionHandler.class);
    TelegramController controller = mock(TelegramController.class);
    Map<String, TelegramController> controllers = new HashMap<>();
    FilterChain filterChain = mock(FilterChain.class);
    TelegramUserService telegramUserService = mock(TelegramUserService.class);

    ControllerHandler controllerHandler;

    ControllerHandlerTest() {
        controllers.put("command", controller);
        controllerHandler = new ControllerHandler(exceptionHandler, controllers, filterChain, telegramUserService, messageSource);
    }

    @Test
    void testHandleCommands_whenExistingCommand_shouldReturnResult() {
        String chatId = "123";
        Update update = getUpdate("command", Long.parseLong(chatId));

        String response = "messages";
        Locale locale = new Locale("");
        when(controller.apply(chatId, update, locale))
                .thenReturn(new SendMessage(chatId, response));


        SendMessage result = controllerHandler.handleCommands(update, locale);


        verify(filterChain, times(1)).handleFilterChain(controller, update);
        assertEquals(chatId, result.getChatId());
        assertEquals(response, result.getText());
    }

    @Test
    void testHandleCommands_whenNotExistingCommand_shouldReturnUnknownCommandMessage() {
        String chatId = "123";
        Update update = getUpdate("notExistingCommand", Long.parseLong(chatId));
        Locale locale = new Locale("");
        when(messageSource.getMessage("unregistered.command.message", null, new Locale("")))
                .thenReturn("unregistered");
        when(messageSource.getMessage("command.error.template.message", null, new Locale("")))
                .thenReturn("template");


        SendMessage result = controllerHandler.handleCommands(update, locale);


        verify(filterChain, times(0)).handleFilterChain(controller, update);
        assertEquals(chatId, result.getChatId());
        assertEquals("unregisteredtemplate", result.getText());
    }

    @Test
    void testHandleCommands_whenFilterChainThrowException_shouldReturnExceptionMessage() {
        String chatId = "123";
        Update update = getUpdate("command", Long.parseLong(chatId));
        RuntimeException exception = new NotExistingMessengerActivationTokenException();
        String exceptionResponse = exception.getMessage();
        Locale locale = new Locale("");

        doThrow(exception).when(filterChain).handleFilterChain(controller, update);
        when(exceptionHandler.handleException(chatId, exception))
                .thenReturn(new SendMessage(chatId, exceptionResponse));


        SendMessage result = controllerHandler.handleCommands(update, locale);


        assertEquals(chatId, result.getChatId());
        assertEquals(new NotExistingMessengerActivationTokenException().getMessage(), result.getText());
    }

    @Test
    void testHandleCommands_whenCommandThrowException_shouldReturnExceptionMessage() {
        String chatId = "123";
        Update update = getUpdate("command", Long.parseLong(chatId));
        RuntimeException exception = new NotExistingMessengerActivationTokenException();
        String exceptionResponse = exception.getMessage();
        Locale locale = new Locale("");

        when(controller.apply(chatId, update, locale))
                .thenThrow(exception);
        when(exceptionHandler.handleException(chatId, exception))
                .thenReturn(new SendMessage(chatId, exceptionResponse));


        SendMessage result = controllerHandler.handleCommands(update, locale);


        verify(filterChain, times(1)).handleFilterChain(controller, update);
        assertEquals(chatId, result.getChatId());
        assertEquals(new NotExistingMessengerActivationTokenException().getMessage(), result.getText());
    }

    private Update getUpdate(String text, long chatId) {
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        long userId = 123456;

        chat.setId(chatId);
        update.setMessage(message);
        message.setText(text);
        message.setChat(chat);
        User user = new User();

        update.setMessage(message);
        message.setFrom(user);
        user.setId(userId);
        user.setLanguageCode("");
        return update;
    }
}