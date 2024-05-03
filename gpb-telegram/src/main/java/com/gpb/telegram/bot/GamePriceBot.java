package com.gpb.telegram.bot;

import com.gpb.telegram.configuration.BotConfiguration;
import com.gpb.telegram.handler.ControllerHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
@AllArgsConstructor
public class GamePriceBot extends TelegramLongPollingBot {

    private final BotConfiguration botConfiguration;
    private final ControllerHandler commandsHandler;


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
        commandsHandler.handleCommands(update)
                .getMessages()
                .forEach(this::sendMessage);
    }

    private void sendMessage(PartialBotApiMethod botApiMethod) {
        try {
            if (botApiMethod instanceof SendPhoto) {
                execute((SendPhoto) botApiMethod);
            } else {
                execute((SendMessage) botApiMethod);
            }
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
