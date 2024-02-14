package com.gpb.telegram.bot;

import com.gpb.telegram.configuration.BotConfiguration;
import com.gpb.telegram.handler.CallbacksHandler;
import com.gpb.telegram.handler.ControllerHandler;
import com.gpb.telegram.util.Consts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
@RequiredArgsConstructor
public class GamePriceBot extends TelegramLongPollingBot {

    public final BotConfiguration botConfiguration;

    public final ControllerHandler commandsHandler;

    public final CallbacksHandler callbacksHandler;


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
            if (isMessageCommand(update)) {
                sendMessage(commandsHandler.handleCommands(update));
            } else {
                sendMessage(new SendMessage(chatId, Consts.CANT_UNDERSTAND));
            }
        } else if (update.hasCallbackQuery()) {
            sendMessage(callbacksHandler.handleCallbacks(update));
        }
    }

    private boolean isMessageNotEmpty(Update update){
        return update.hasMessage() && update.getMessage().hasText();
    }

    private boolean isMessageCommand(Update update){
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
