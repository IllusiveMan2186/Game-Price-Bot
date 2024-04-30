package com.gpb.telegram.handler;

import com.gpb.telegram.controller.TelegramController;
import com.gpb.telegram.controller.impl.HelpController;
import com.gpb.telegram.filter.FilterChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;
import java.util.Map;

@Component
@Slf4j
public class ControllerHandler {

    private final ExceptionHandler exceptionHandler;
    private final Map<String, TelegramController> controllers;
    private final FilterChain filterChain;
    private final MessageSource messageSource;

    public ControllerHandler(ExceptionHandler exceptionHandler, Map<String, TelegramController> controllers,
                             FilterChain filterChain, MessageSource messageSource) {
        this.exceptionHandler = exceptionHandler;
        this.messageSource = messageSource;
        HelpController helpController = new HelpController(controllers, this.messageSource);
        controllers.put("help", helpController);
        this.controllers = controllers;
        this.filterChain = filterChain;
    }


    public SendMessage handleCommands(Update update, Locale locale) {
        String messageText = update.getMessage().getText();
        String commandName = messageText.split(" ")[0].replace("/", "");
        String chatId = String.valueOf(update.getMessage().getChatId());

        TelegramController controller = controllers.get(commandName);
        if (controller != null) {
            try {
                filterChain.handleFilterChain(controller, update);
                return controller.apply(chatId, update, locale);
            } catch (Exception exception) {
                log.warn("Exception during command execution:" + exception.getMessage());
                return exceptionHandler.handleException(chatId, exception);
            }
        } else {
            log.info("Unknown message:" + messageText);
            String response = messageSource.getMessage("unregistered.command.message", null, locale) +
                    messageSource.getMessage("command.error.template.message", null, locale);
            return new SendMessage(chatId, response);
        }
    }
}
