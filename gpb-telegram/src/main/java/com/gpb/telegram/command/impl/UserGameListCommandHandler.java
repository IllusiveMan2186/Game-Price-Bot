package com.gpb.telegram.command.impl;

import com.gpb.telegram.command.CommandHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.service.CommonRequestHandlerService;
import com.gpb.telegram.util.Constants;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component("userGameList")
@AllArgsConstructor
@FilterChainMarker(Constants.USER_EXISTING_FILTER)
public class UserGameListCommandHandler implements CommandHandler {

    private final MessageSource messageSource;
    private final CommonRequestHandlerService commonRequestHandlerService;

    @Override
    public String getDescription(Locale locale) {
        return messageSource.getMessage("game.user.game.list.command.description", null, locale);
    }

    @Override
    public TelegramResponse apply(TelegramRequest request) {
        return commonRequestHandlerService.processGameListRequest(request, 1);
    }
}
