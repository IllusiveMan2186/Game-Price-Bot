package com.gpb.telegram.callback.impl;

import com.gpb.telegram.callback.CallbackHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.service.CommonRequestHandlerService;
import com.gpb.telegram.util.Constants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("userGameListCallback")
@RequiredArgsConstructor
@FilterChainMarker(Constants.USER_EXISTING_FILTER)
public class UserGameListCallbackHandler implements CallbackHandler {

    private final CommonRequestHandlerService commonRequestHandlerService;

    @Transactional
    @Override
    public TelegramResponse apply(TelegramRequest request) {
        int pageNum = request.getIntArgument(1);
        return commonRequestHandlerService.processGameListRequest(request, pageNum);
    }
}
