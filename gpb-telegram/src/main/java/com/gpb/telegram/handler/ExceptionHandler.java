package com.gpb.telegram.handler;

import com.gpb.telegram.bean.TelegramResponse;
import org.springframework.stereotype.Component;

@Component
public class ExceptionHandler {

    public TelegramResponse handleException(String chatId, Exception e){
        e.printStackTrace();
        return new TelegramResponse(chatId, e.getMessage());
    }
}
