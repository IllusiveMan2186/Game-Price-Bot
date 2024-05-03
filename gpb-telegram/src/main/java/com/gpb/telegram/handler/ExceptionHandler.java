package com.gpb.telegram.handler;

import com.gpb.telegram.bean.TelegramResponse;
import org.springframework.stereotype.Component;

@Component
public class ExceptionHandler {

    public TelegramResponse handleException(String chatId, Exception e){

        return new TelegramResponse(chatId, e.getMessage());
    }
}
