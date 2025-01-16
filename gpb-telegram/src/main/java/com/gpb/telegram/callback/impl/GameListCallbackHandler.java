package com.gpb.telegram.callback.impl;

import com.gpb.telegram.callback.CallbackHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.service.CommonRequestHandlerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component("gameListCallback")
@AllArgsConstructor
public class GameListCallbackHandler implements CallbackHandler {

    private final CommonRequestHandlerService commonRequestHandlerService;

    @Override
    public TelegramResponse apply(TelegramRequest request) {
        int pageNum = request.getIntArgument(1);
        String sort = request.getArgument(2);
        return commonRequestHandlerService.processGameListRequest(request, pageNum, sort);
    }
}
