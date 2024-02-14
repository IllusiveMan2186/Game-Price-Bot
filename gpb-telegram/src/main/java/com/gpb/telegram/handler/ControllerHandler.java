package com.gpb.telegram.handler;

import com.gpb.telegram.controller.TelegramController;
import com.gpb.telegram.util.Consts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@Component
@Slf4j
public class ControllerHandler {

    private final Map<String, TelegramController> controllers;

    public ControllerHandler(Map<String, TelegramController> controllers) {
        this.controllers = controllers;
    }


    public SendMessage handleCommands(Update update) {
        String messageText = update.getMessage().getText();
        String commandName = messageText.split(" ")[0];
        long chatId = update.getMessage().getChatId();

        TelegramController controller = controllers.get(commandName);
        if (controller != null) {
            return controller.apply(update);
        } else {
            return new SendMessage(String.valueOf(chatId), Consts.UNKNOWN_COMMAND);
        }
    }
}
