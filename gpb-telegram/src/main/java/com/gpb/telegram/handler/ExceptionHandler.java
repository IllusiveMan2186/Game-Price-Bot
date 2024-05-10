package com.gpb.telegram.handler;

import com.gpb.telegram.bean.TelegramRequest;
import com.gpb.telegram.bean.TelegramResponse;
import org.springframework.stereotype.Component;

@Component
public class ExceptionHandler {

    public TelegramResponse handleException(TelegramRequest request, Exception e){
        e.printStackTrace();
        return new TelegramResponse(request, e.getMessage());
    }
}
