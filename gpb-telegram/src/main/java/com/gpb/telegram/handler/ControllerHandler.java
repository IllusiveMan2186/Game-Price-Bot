package com.gpb.telegram.handler;

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


    public TelegramResponse handleCommands(Update update) {
        Locale locale = getUserLocale(update);
        String chatId = getChatId(update);
        String commandName = getCommandName(update);

        if (isMessageCommand(update)) {
            try {
                FilteredHandler handler = isMessageNotEmpty(update)
                        ? controllers.get(commandName)
                        : callbackHandlerMap.get(commandName);
                if (handler == null) {
                    log.info("Unknown message:" + update.getMessage().getText());
                    String response = messageSource.getMessage("unregistered.command.message", null, locale) +
                            messageSource.getMessage("command.error.template.message", null, locale);
                    return new TelegramResponse(chatId, response);
                }
                filterChain.handleFilterChain(handler, update);

                return handler.apply(chatId, update, locale);
            } catch (Exception exception) {
                log.warn("Exception during command execution:" + exception.getMessage());
                return exceptionHandler.handleException(chatId, exception);
            }
        } else {
            String response = messageSource.getMessage("command.not.found.message", null, locale) +
                    messageSource.getMessage("command.error.template.message", null, locale);
            return new TelegramResponse(chatId, response);
        }
    }

    private String getChatId(Update update) {
        return update.hasCallbackQuery()
                ? update.getCallbackQuery().getFrom().getId().toString()
                : update.getMessage().getFrom().getId().toString();
    }

    private Locale getUserLocale(Update update) {
        long userId = update.hasCallbackQuery()
                ? update.getCallbackQuery().getFrom().getId()
                : update.getMessage().getFrom().getId();
        if (!telegramUserService.isUserRegistered(userId)) {
            return update.hasCallbackQuery()
                    ? new Locale(update.getCallbackQuery().getFrom().getLanguageCode())
                    : new Locale(update.getMessage().getFrom().getLanguageCode());

        }
        return telegramUserService.getUserLocale(userId);

    }

    private boolean isMessageNotEmpty(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    private boolean isMessageCommand(Update update) {
        return update.hasCallbackQuery()
                ? update.getCallbackQuery().getData().startsWith("/")
                : update.getMessage().getText().startsWith("/");
    }


    private String getCommandName(Update update) {
        return update.hasCallbackQuery()
                ? update.getCallbackQuery().getData().split(" ")[0].replace("/", "")
                : update.getMessage().getText().split(" ")[0].replace("/", "");
    }
}
