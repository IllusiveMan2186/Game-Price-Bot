package com.gpb.telegram.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Data
public class TelegramResponse {

    public TelegramResponse(PartialBotApiMethod method) {
        this.messages = Collections.singletonList(method);
    }

    public TelegramResponse(String chatId, String message) {
        this.messages = Collections.singletonList(new SendMessage(chatId, message));
    }

    public TelegramResponse(TelegramRequest request, String message) {
        this.messages = Collections.singletonList(new SendMessage(request.getChatId(), message));
    }

    private List<PartialBotApiMethod> messages;
}
