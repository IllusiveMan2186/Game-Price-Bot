package com.gpb.telegram.handler;

import com.gpb.telegram.bean.TelegramRequest;
import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.callback.CallbackHandler;
import com.gpb.telegram.command.CommandHandler;
import com.gpb.telegram.command.impl.HelpCommandHandler;
import com.gpb.telegram.filter.FilterChain;
import com.gpb.telegram.filter.FilteredHandler;
import com.gpb.telegram.service.TelegramUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;
import java.util.Map;

@Component
@Slf4j
public class ControllerHandler {

    private final Map<String, CommandHandler> controllers;
    private final Map<String, CallbackHandler> callbackHandlerMap;
    private final MessageSource messageSource;
    private final TelegramUserService telegramUserService;
    private final ExceptionHandler exceptionHandler;
    private final FilterChain filterChain;

    public ControllerHandler(Map<String, CommandHandler> controllers, Map<String, CallbackHandler> callbackHandlerMap,
                             MessageSource messageSource, TelegramUserService telegramUserService,
                             ExceptionHandler exceptionHandler, FilterChain filterChain) {
        this.callbackHandlerMap = callbackHandlerMap;
        this.messageSource = messageSource;
        this.telegramUserService = telegramUserService;
        this.exceptionHandler = exceptionHandler;
        this.filterChain = filterChain;
        HelpCommandHandler helpCommandHandler = new HelpCommandHandler(this.messageSource, controllers);
        controllers.put("help", helpCommandHandler);
        this.controllers = controllers;
    }


    public TelegramResponse handleCommands(TelegramRequest request) {
        request.setLocale(getUserLocale(request));

        if (isMessageCommand(request.getUpdate())) {
            try {
                FilteredHandler handler = isMessageNotEmpty(request.getUpdate())
                        ? controllers.get(request.getCommandName())
                        : callbackHandlerMap.get(request.getCommandName());
                if (handler == null) {
                    log.info("Unknown message:" + request.getUpdate().getMessage().getText());
                    String response = messageSource.getMessage("unregistered.command.message", null, request.getLocale()) +
                            messageSource.getMessage("command.error.template.message", null, request.getLocale());
                    return new TelegramResponse(request, response);
                }
                filterChain.handleFilterChain(handler, request);

                return handler.apply(request);
            } catch (Exception exception) {
                log.warn("Exception during command execution:" + exception.getMessage());
                return exceptionHandler.handleException(request, exception);
            }
        } else {
            String response = messageSource.getMessage("command.not.found.message", null, request.getLocale()) +
                    messageSource.getMessage("command.error.template.message", null, request.getLocale());
            return new TelegramResponse(request, response);
        }
    }

    private Locale getUserLocale(TelegramRequest request) {
        long userId = request.getUserId();
        return !telegramUserService.isUserRegistered(userId)
                ? new Locale(request.getFrom().getLanguageCode())
                : telegramUserService.getUserLocale(userId);


    }

    private boolean isMessageNotEmpty(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    private boolean isMessageCommand(Update update) {
        return update.hasCallbackQuery()
                ? update.getCallbackQuery().getData().startsWith("/")
                : update.getMessage().getText().startsWith("/");
    }
}
