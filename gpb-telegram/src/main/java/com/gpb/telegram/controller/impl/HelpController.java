package com.gpb.telegram.controller.impl;

import com.gpb.telegram.controller.TelegramController;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

public class HelpController implements TelegramController {

    private static final String HEADER = "You could use one of available commands:";

    private final String helpText;

    public HelpController(Map<String, TelegramController> controllers){
        StringBuilder builder = new StringBuilder(HEADER);
        for (Map.Entry<String, TelegramController> entrySet: controllers.entrySet()) {
            builder.append(System.lineSeparator())
                    .append("/").append(entrySet.getKey())
                    .append(entrySet.getValue().getDescription());
        }
        this.helpText = builder.toString();
    }

    @Override
    public String getDescription() {
        return " - help command";
    }

    @Override
    public SendMessage apply(String chatId, Update update) {
        return new SendMessage(chatId, helpText);
    }
}
