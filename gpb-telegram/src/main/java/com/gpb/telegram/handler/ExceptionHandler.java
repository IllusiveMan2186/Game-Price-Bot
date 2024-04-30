package com.gpb.telegram.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class ExceptionHandler {

    public SendMessage handleException(String chatId, Exception e){

        return new SendMessage(chatId, e.getMessage());
    }
}
