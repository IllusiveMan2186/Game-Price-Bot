package com.gpb.telegram.bot;

import com.gpb.telegram.configuration.BotConfiguration;
import com.gpb.telegram.handler.CallbacksHandler;
import com.gpb.telegram.handler.ControllerHandler;
import com.gpb.telegram.service.TelegramUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Locale;

@Component
@Slf4j
@RequiredArgsConstructor
public class GamePriceBot extends TelegramLongPollingBot {

    private final BotConfiguration botConfiguration;
    private final ControllerHandler commandsHandler;
    private final CallbacksHandler callbacksHandler;
    private final MessageSource messageSource;
    private final TelegramUserService telegramUserService;

    @Override
    public String getBotUsername() {
        return botConfiguration.getName();
    }

    @Override
    public String getBotToken() {
        return botConfiguration.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (isMessageNotEmpty(update)) {
            String chatId = update.getMessage().getChatId().toString();
            Locale locale = getUserLocale(update);
            if (isMessageCommand(update)) {
                sendMessage(commandsHandler.handleCommands(update, locale));
            } else {
                String response = messageSource.getMessage("command.not.found.message", null, locale) +
                        messageSource.getMessage("command.error.template.message", null, locale);
                sendMessage(new SendMessage(chatId, response));
            }
        } else if (update.hasCallbackQuery()) {
            sendMessage(callbacksHandler.handleCallbacks(update));
        }
    }

    private Locale getUserLocale(Update update) {
        long userId = update.getMessage().getFrom().getId();
        return !telegramUserService.isUserRegistered(userId)
                ? new Locale(update.getMessage().getFrom().getLanguageCode())
                : telegramUserService.getUserLocale(userId);

    }

    private boolean isMessageNotEmpty(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    private boolean isMessageCommand(Update update) {
        return update.getMessage().getText().startsWith("/");
    }

    private void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
