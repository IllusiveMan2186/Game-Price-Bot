package com.gpb.telegram.handler;

import com.gpb.telegram.callback.CallbackHandler;
import com.gpb.telegram.command.CommandHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.exception.NotExistingAccountSynchronizationException;
import com.gpb.telegram.filter.FilterChain;
import com.gpb.telegram.service.TelegramUserService;
import com.gpb.telegram.util.UpdateCreator;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
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
    CommandHandler controller = mock(CommandHandler.class);
    Map<String, CommandHandler> commandHandlerMap = new HashMap<>();
    CallbackHandler callbackHandler = mock(CallbackHandler.class);
    Map<String, CallbackHandler> callbackHandlerMap = new HashMap<>();
    FilterChain filterChain = mock(FilterChain.class);
    TelegramUserService telegramUserService = mock(TelegramUserService.class);

    ControllerHandler controllerHandler;

    ControllerHandlerTest() {
        commandHandlerMap.put("command", controller);
        callbackHandlerMap.put("callbackCommand", callbackHandler);
        controllerHandler = new ControllerHandler(commandHandlerMap, callbackHandlerMap, messageSource,
                telegramUserService, exceptionHandler, filterChain);
    }

    @Test
    void testHandleCommands_whenExistingCommand_shouldReturnResult() {
        String chatId = "123456";
        Update update = UpdateCreator.getUpdateWithoutCallback("/command", Long.parseLong(chatId));

        String message = "messages";
        Locale locale = new Locale("");
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).build();
        when(controller.apply(request)).thenReturn(
                new TelegramResponse(Collections.singletonList(new SendMessage(chatId, message))));


        TelegramResponse response = controllerHandler.handleCommands(request);


        SendMessage sendMessage = (SendMessage) response.getMessages().get(0);
        verify(filterChain, times(1)).handleFilterChain(controller, request);
        assertEquals(chatId, sendMessage.getChatId());
        assertEquals(message, sendMessage.getText());
    }

    @Test
    void testHandleCommands_whenExistingUser_shouldReturnResult() {
        String chatId = "123456";
        Update update = UpdateCreator.getUpdateWithoutCallback("/command", Long.parseLong(chatId));

        String message = "messages";
        Locale locale = new Locale("");
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).build();
        when(controller.apply(request)).thenReturn(
                new TelegramResponse(Collections.singletonList(new SendMessage(chatId, message))));
        when(telegramUserService.isUserRegistered(123456)).thenReturn(true);
        when(telegramUserService.getUserLocale(123456)).thenReturn(locale);

        TelegramResponse response = controllerHandler.handleCommands(request);


        SendMessage sendMessage = (SendMessage) response.getMessages().get(0);
        verify(filterChain, times(1)).handleFilterChain(controller, request);
        assertEquals(chatId, sendMessage.getChatId());
        assertEquals(message, sendMessage.getText());
    }

    @Test
    void testHandleCallback_whenExistingCommand_shouldReturnResult() {
        String chatId = "123456";
        Update update = UpdateCreator.getUpdateWithCallback("/callbackCommand", Long.parseLong(chatId));

        String message = "messages";
        Locale locale = new Locale("");
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).build();
        when(callbackHandler.apply(request)).thenReturn(new TelegramResponse(chatId, message));


        TelegramResponse response = controllerHandler.handleCommands(request);


        SendMessage sendMessage = (SendMessage) response.getMessages().get(0);
        verify(filterChain, times(1)).handleFilterChain(callbackHandler, request);
        assertEquals(chatId, sendMessage.getChatId());
        assertEquals(message, sendMessage.getText());
    }

    @Test
    void testHandleCommands_whenNotExistingCommand_shouldReturnUnknownCommandMessage() {
        String chatId = "123456";
        Update update = UpdateCreator.getUpdateWithoutCallback("/notExistingCommand", Long.parseLong(chatId));
        Locale locale = new Locale("");
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).build();
        when(messageSource.getMessage("unregistered.command.message", null, new Locale("")))
                .thenReturn("unregistered");
        when(messageSource.getMessage("command.error.template.message", null, new Locale("")))
                .thenReturn("template");


        TelegramResponse response = controllerHandler.handleCommands(request);


        SendMessage sendMessage = (SendMessage) response.getMessages().get(0);
        verify(filterChain, times(0)).handleFilterChain(controller, request);
        assertEquals(chatId, sendMessage.getChatId());
        assertEquals("unregisteredtemplate", sendMessage.getText());
    }

    @Test
    void testHandleCommands_whenNoCommand_shouldReturnNoCommandMessage() {
        String chatId = "123456";
        Update update = UpdateCreator.getUpdateWithoutCallback("notCommand", Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).build();
        when(messageSource.getMessage("command.not.found.message", null, new Locale("")))
                .thenReturn("noCommand");
        when(messageSource.getMessage("command.error.template.message", null, new Locale("")))
                .thenReturn("template");


        TelegramResponse response = controllerHandler.handleCommands(request);


        SendMessage sendMessage = (SendMessage) response.getMessages().get(0);
        verify(filterChain, times(0)).handleFilterChain(controller, request);
        assertEquals(chatId, sendMessage.getChatId());
        assertEquals("noCommandtemplate", sendMessage.getText());
    }

    @Test
    void testHandleCommands_whenFilterChainThrowException_shouldReturnExceptionMessage() {
        String chatId = "123456";
        Update update = UpdateCreator.getUpdateWithoutCallback("/command", Long.parseLong(chatId));
        RuntimeException exception = new NotExistingAccountSynchronizationException();
        String exceptionResponse = exception.getMessage();
        TelegramRequest request = TelegramRequest.builder().update(update).build();
        doThrow(exception).when(filterChain).handleFilterChain(controller, request);
        when(exceptionHandler.handleException(request, exception))
                .thenReturn(new TelegramResponse(chatId, exceptionResponse));


        TelegramResponse response = controllerHandler.handleCommands(request);


        SendMessage sendMessage = (SendMessage) response.getMessages().get(0);
        assertEquals(chatId, sendMessage.getChatId());
        assertEquals(new NotExistingAccountSynchronizationException().getMessage(), sendMessage.getText());
    }

    @Test
    void testHandleCommands_whenCommandThrowException_shouldReturnExceptionMessage() {
        String chatId = "123456";
        Update update = UpdateCreator.getUpdateWithoutCallback("/command", Long.parseLong(chatId));
        RuntimeException exception = new NotExistingAccountSynchronizationException();
        String exceptionResponse = exception.getMessage();
        Locale locale = new Locale("");
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).build();

        when(controller.apply(request))
                .thenThrow(exception);
        when(exceptionHandler.handleException(request, exception))
                .thenReturn(new TelegramResponse(chatId, exceptionResponse));


        TelegramResponse response = controllerHandler.handleCommands(request);


        SendMessage sendMessage = (SendMessage) response.getMessages().get(0);
        verify(filterChain, times(1)).handleFilterChain(controller, request);
        assertEquals(chatId, sendMessage.getChatId());
        assertEquals(new NotExistingAccountSynchronizationException().getMessage(), sendMessage.getText());
    }

}