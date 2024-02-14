package com.gpb.telegram.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;


@Component
public class CallbacksHandler {

    public SendMessage handleCallbacks(Update update) {
        return null;
    }

}
